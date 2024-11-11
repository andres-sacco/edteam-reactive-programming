package com.edteam.reservations.connector;

import com.edteam.reservations.connector.configuration.EndpointConfiguration;
import com.edteam.reservations.connector.configuration.HostConfiguration;
import com.edteam.reservations.connector.configuration.HttpConnectorConfiguration;
import com.edteam.reservations.connector.response.CityDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public CityDTO getCity(String code) {
        LOGGER.info("calling to api-catalog");

        HostConfiguration hostConfiguration = configuration.getHosts().get(HOST);
        EndpointConfiguration endpointConfiguration = hostConfiguration.getEndpoints().get(ENDPOINT);

        return new CityDTO();
    }
}