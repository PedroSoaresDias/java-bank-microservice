package br.com.bank.account_service.service;

import br.com.bank.account_service.domain.DTO.UserDTO;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserDTO> getUserById(Long id);
}
