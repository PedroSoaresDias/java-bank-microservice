package br.com.bank.auth_service.service;

import br.com.bank.auth_service.domain.DTO.UserDTO;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserDTO> getUserByEmail(String email);
}
