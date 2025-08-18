package br.com.bank.user_service.service;

import br.com.bank.user_service.domain.DTO.UserRequest;
import br.com.bank.user_service.domain.DTO.UserResponse;
import br.com.bank.user_service.domain.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Flux<UserResponse> findUsersPaginated(int page, int limit);

    Mono<UserResponse> findUserById(Long id);

    Mono<User> findUserByEmail(String email);

    Mono<UserResponse> createUser(UserRequest request);

    Mono<UserResponse> updateUser(Long id, UserRequest request);

    Mono<Void> deleteUser(Long id);
}
