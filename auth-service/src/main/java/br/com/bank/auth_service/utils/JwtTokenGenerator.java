package br.com.bank.auth_service.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenGenerator {
    private String secret;
    private long expiration;

    public JwtTokenGenerator(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    public String generateToken(Long id) {
        try {
            return JWT.create()
            .withSubject(id.toString())
            .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                    .sign(Algorithm.HMAC256(secret));
        } catch (JWTCreationException e) {
            log.warn("Falha na criação do token: {}", e.getMessage());
            return null;
        }
    }
}
