package br.com.bank.investment_service.adapter;

import reactor.core.publisher.Mono;

public interface HttpClientAdapter {
    <T> Mono<T> get(String url, Class<T> responseType);

    <T, R> Mono<R> post(String url, T requestBody, Class<R> responseType);
}
