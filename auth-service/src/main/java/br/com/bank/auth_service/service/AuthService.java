package br.com.bank.auth_service.service;

import br.com.bank.auth_service.domain.DTO.AuthRequest;
import br.com.bank.auth_service.domain.DTO.AuthResponse;
import reactor.core.publisher.Mono;

public interface AuthService {
    Mono<AuthResponse> authenticate(AuthRequest request);
}
