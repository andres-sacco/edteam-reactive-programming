package com.edteam.reservations.configuration;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public class ReactiveAuthenticationManagerAdapter implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.fromCallable(() -> authentication).onErrorResume(e -> Mono.empty()); // Optionally handle errors if
                                                                                         // needed
    }
}
