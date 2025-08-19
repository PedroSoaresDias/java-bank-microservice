package br.com.bank.account_service.domain.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateAccountRequest(
        @NotBlank(message = "A chave pix não pode está em branco") 
        String pix,
                
        @PositiveOrZero(message = "Saldo inicial não pode ser negativo")
        BigDecimal balance) {

}
