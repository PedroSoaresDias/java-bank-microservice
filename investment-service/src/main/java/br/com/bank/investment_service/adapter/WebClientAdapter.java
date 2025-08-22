package br.com.bank.investment_service.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import br.com.bank.investment_service.utils.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class WebClientAdapter implements HttpClientAdapter {
    private final WebClient webClient;

    public WebClientAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public <T> Mono<T> get(String url, Class<T> responseType) {
        return SecurityUtil.getCurrentUserId()
                .flatMap(userId -> webClient.get()
                        .uri(url)
                        .header("X-User-Id", userId.toString())
                        .retrieve()
                        .bodyToMono(responseType)
                        .onErrorMap(
                                ex -> new RuntimeException("Erro ao acessar serviço remoto: " + ex.getMessage(), ex)));

    }

    @Override
    public <T, R> Mono<R> post(String url, T requestBody, Class<R> responseType) {
        return SecurityUtil.getCurrentUserId()
                .flatMap(userId -> webClient.post()
                        .uri(url)
                        .header("X-User-Id", userId.toString())
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(responseType)
                        .onErrorMap(
                                ex -> new RuntimeException("Erro ao acessar serviço remoto: " + ex.getMessage(), ex)));

    }

}
