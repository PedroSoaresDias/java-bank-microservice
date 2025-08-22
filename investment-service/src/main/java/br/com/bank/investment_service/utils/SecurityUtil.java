package br.com.bank.investment_service.utils;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import reactor.core.publisher.Mono;

public class SecurityUtil {
    public static Mono<Long> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth != null && auth.isAuthenticated())
                .flatMap(auth -> {
                    Object principal = auth.getPrincipal();
                    try {
                        return Mono.just(Long.valueOf(principal.toString()));
                    } catch (Exception e) {
                        return Mono.error(new IllegalStateException("UserId inválido no principal"));
                    }
                });

    }

    // public static Mono<String> getCurrentToken() {
    //     return ReactiveSecurityContextHolder.getContext()
    //     .map(SecurityContext::getAuthentication)
    //     .filter(auth -> auth != null && auth.isAuthenticated())
    //     .map(auth -> {
    //         Object credentials = auth.getCredentials();
    //         return credentials != null ? credentials.toString() : null;
    //     });

    // }
}
