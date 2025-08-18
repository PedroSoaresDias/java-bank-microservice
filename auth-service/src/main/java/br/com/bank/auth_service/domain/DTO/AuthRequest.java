package br.com.bank.auth_service.domain.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @Email(message = "Email inválido") String email,
        @NotBlank(message = "Senha é obrigatória") String password) {

}
