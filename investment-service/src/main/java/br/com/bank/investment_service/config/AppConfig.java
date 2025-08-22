package br.com.bank.investment_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.bank.investment_service.utils.SecurityUtil;

@Configuration
public class AppConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
        .filter((request, next) -> {
            return SecurityUtil.getCurrentUserId()
                .map(userId -> ClientRequest.from(request)
                    .header("X-User-Id", userId.toString())
                    .build())
                .flatMap(next::exchange);
        })
        .build();

    }
}
