package br.com.bank.user_service.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.bank.user_service.domain.DTO.UserRequest;
import br.com.bank.user_service.domain.DTO.UserResponse;
import br.com.bank.user_service.domain.model.User;
import br.com.bank.user_service.domain.repository.UserRepository;
import br.com.bank.user_service.exceptions.UserNotFoundException;
import br.com.bank.user_service.service.UserService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Flux<UserResponse> findAllUsers() {
        return userRepository.findAll().map(this::toDTO);
    }

    @Override
    public Mono<UserResponse> findUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuário não encontrado")));
    }

    @Override
    public Mono<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuário não encontrado")));
    }

    @Override
    public Mono<Void> createUser(UserRequest request) {
        User user = new User();

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        return userRepository.save(user).then();
    }

    @Override
    public Mono<Void> updateUser(Long id, UserRequest request) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuário não encontrado")))
                .flatMap(user -> {
                    user.setName(request.name());
                    user.setEmail(request.email());
                    
                    if (request.password() != null) {
                        user.setPassword(passwordEncoder.encode(request.password()));
                    }

                    return userRepository.save(user);
                })
                .then();
    }

    @Override
    public Mono<Void> deleteUser(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuário não encontrado")))
                .flatMap(userRepository::delete)
                .then();
    }

    private UserResponse toDTO(User user) {
        return new UserResponse(user.getId(), user.getName());
    }
}
