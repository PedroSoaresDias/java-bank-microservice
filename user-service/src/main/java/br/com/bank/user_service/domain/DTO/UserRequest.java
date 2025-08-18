package br.com.bank.user_service.domain.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank(message = "Nome é obrigatório") String name,
        @Email(message = "Email inválido") String email,
        @NotBlank(message = "Senha é obrigatória") String password) {

}
