package br.com.bank.auth_service.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class WebClientAdapter implements HttpClientAdapter {
    private final WebClient webClient;

    public WebClientAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public <T> Mono<T> get(String url, Class<T> responseType) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorMap(ex -> new RuntimeException("Erro ao acessar serviço remoto: " + ex.getMessage(), ex));
    }

}
