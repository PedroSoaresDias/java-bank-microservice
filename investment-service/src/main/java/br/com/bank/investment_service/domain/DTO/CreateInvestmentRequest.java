package br.com.bank.investment_service.domain.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateInvestmentRequest(
        @NotBlank(message = "A chave pix não pode está em branco") 
        String pix,
                               
        @Positive(message = "O valor da taxa deve ser positivo")
        BigDecimal tax,
                
        @Positive(message = "O valor em dinheiro deve ser positivo")
        BigDecimal amount
) {

}
