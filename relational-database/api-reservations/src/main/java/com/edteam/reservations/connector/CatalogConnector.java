package com.edteam.reservations.connector;

import com.edteam.reservations.connector.configuration.EndpointConfiguration;
import com.edteam.reservations.connector.configuration.HostConfiguration;
import com.edteam.reservations.connector.configuration.HttpConnectorConfiguration;
import com.edteam.reservations.connector.response.CityDTO;
import com.edteam.reservations.enums.APIError;
import com.edteam.reservations.exception.EdteamException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.handler.timeout.WriteTimeoutHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class CatalogConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogConnector.class);
    private final String HOST = "api-catalog";

    private final String ENDPOINT = "get-city";

    private HttpConnectorConfiguration configuration;

    @Autowired
    public CatalogConnector(HttpConnectorConfiguration configuration) {
        this.configuration = configuration;
    }

    public Mono<CityDTO> getCity(String code) {
        LOGGER.info("calling to api-catalog");

        HostConfiguration hostConfiguration = configuration.getHosts().get(HOST);
        EndpointConfiguration endpointConfiguration = hostConfiguration.getEndpoints().get(ENDPOINT);

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        Math.toIntExact(endpointConfiguration.getConnectionTimeout()))
                .doOnConnected(conn -> conn
                        .addHandler(
                                new ReadTimeoutHandler(endpointConfiguration.getReadTimeout(), TimeUnit.MILLISECONDS))
                        .addHandler(new WriteTimeoutHandler(endpointConfiguration.getWriteTimeout(),
                                TimeUnit.MILLISECONDS)))
                //.protocol(HttpProtocol.H2) // Forzar HTTP2 solo cuando el cliente usa tambien http2
                .compress(true)
                .http2Settings(settings -> settings
                        .maxConcurrentStreams(200)
                        .initialWindowSize(131072)
                        .maxFrameSize(32768)
                );

        WebClient client = WebClient.builder()
                .baseUrl("http://" + hostConfiguration.getHost() + ":" + hostConfiguration.getPort()
                        + endpointConfiguration.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();

        return client.get()
                .uri(urlEncoder -> urlEncoder.build(code))
                .retrieve()
                .bodyToMono(CityDTO.class)

                // Mapear un error específico a una excepción personalizada
                .onErrorMap(RuntimeException.class, ex -> new EdteamException(APIError.BAD_FORMAT))

                // Primer tipo de retry: reintenta automáticamente 3 veces para cualquier error
                .retry(3)

                // Segundo tipo de retry: usa un backoff con incremento exponencial para evitar sobrecarga
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .maxBackoff(Duration.ofSeconds(5)) // Límite máximo de espera entre reintentos
                        .filter(ex -> ex instanceof TimeoutException) // Reintento solo si es un TimeoutException
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new EdteamException(APIError.TIMEOUT_EXCEEDED))) // Lanza una excepción específica si se agotan los intentos

                // Tercer tipo de retry: reintentos condicionales según la respuesta de error
                .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2))
                        .filter(ex -> ex instanceof EdteamException)) // Solo reintenta si es EdteamException

                // Manejador de errores: registra cualquier error en el flujo
                .doOnError(error ->
                        LOGGER.error("Error al realizar la solicitud: {}", error.getMessage()))

                // Redirige a una fuente alternativa en caso de error
                .onErrorResume(error -> {
                    LOGGER.error("Error en la solicitud, obteniendo la información de otro lugar.");
                    return client.get().uri(urlEncoder -> urlEncoder.build(code))
                            .retrieve()
                            .bodyToMono(CityDTO.class);
                })

                // Retorna un valor predeterminado si todos los reintentos fallan
                .onErrorReturn(new CityDTO("Default City", "N/A", "N/A"));
    }
}
