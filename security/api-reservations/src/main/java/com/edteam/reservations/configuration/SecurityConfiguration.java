package com.edteam.reservations.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, JwtTokenProvider tokenProvider,
            ReactiveAuthenticationManager reactiveAuthenticationManager) {

        return http.csrf(ServerHttpSecurity.CsrfSpec::disable).authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(auth -> auth.pathMatchers("/documentation/**").permitAll() // Public endpoints
                        .pathMatchers("/swagger-ui.html").permitAll() // Public endpoints
                        .pathMatchers("/swagger-ui/**").permitAll() // Public endpoints
                        .pathMatchers("/v3/api-docs/**").permitAll() // Public endpoints
                        .anyExchange().authenticated() // Protected endpoints
                ).addFilterAt(new JwtTokenAuthenticationFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new ReactiveAuthenticationManagerAdapter();
    }
}