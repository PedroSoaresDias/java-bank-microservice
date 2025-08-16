package br.com.bank.auth_service.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.bank.auth_service.adapter.HttpClientAdapter;
import br.com.bank.auth_service.domain.DTO.UserDTO;
import br.com.bank.auth_service.service.UserService;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
    private String userServiceUrl;
    private final HttpClientAdapter httpClient;

    public UserServiceImpl(@Value("${user.service.url}") String userServiceUrl, HttpClientAdapter httpClient) {
        this.userServiceUrl = userServiceUrl;
        this.httpClient = httpClient;
    }

    @Override
    public Mono<UserDTO> getUserByEmail(String email) {
        String url = userServiceUrl + "/users/email/" + email;
        return httpClient.get(url, UserDTO.class);
    }

}
