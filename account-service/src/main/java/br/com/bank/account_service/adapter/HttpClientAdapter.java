package br.com.bank.account_service.adapter;

import reactor.core.publisher.Mono;

public interface HttpClientAdapter {
    <T> Mono<T> get(String url, Class<T> responseType);
}
