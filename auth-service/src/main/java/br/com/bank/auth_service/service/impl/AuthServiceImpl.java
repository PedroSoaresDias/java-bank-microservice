package br.com.bank.auth_service.service.impl;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.bank.auth_service.domain.DTO.AuthRequest;
import br.com.bank.auth_service.domain.DTO.AuthResponse;
import br.com.bank.auth_service.exceptions.UnauthorizatedAccessException;
import br.com.bank.auth_service.service.AuthService;
import br.com.bank.auth_service.service.UserService;
import br.com.bank.auth_service.utils.JwtTokenGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenGenerator jwtGenerator;

    @Override
    public Mono<AuthResponse> authenticate(AuthRequest request) {
        return userService.getUserByEmail(request.email())
        .doOnNext(user -> log.debug("Tentando autenticar usuário com email: {}", user.email()))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Usuário não encontrado.")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.password(), user.password())) {
                        log.warn("Senha inválida para usuário: {}", request.password());
                        throw new UnauthorizatedAccessException("Credenciais inválidas");
                    }

                    String token = jwtGenerator.generateToken(user.id());
                    return Mono.just(new AuthResponse(token))
                    .doOnNext(response -> log.debug("Usuário autenticado: {}", response));
                });

    }

}
