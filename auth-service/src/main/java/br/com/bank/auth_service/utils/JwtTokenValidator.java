package br.com.bank.auth_service.utils;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenValidator {
    private String secret;

    public JwtTokenValidator(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    public boolean validateToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            return !jwt.getExpiresAt().before(new Date());
        } catch (JWTVerificationException e) {
            log.warn("Falha na validação do token: {}", e.getMessage());
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            log.warn("Erro ao extrair username do token: {}", e.getMessage());
            return null;
        }
    }
}
