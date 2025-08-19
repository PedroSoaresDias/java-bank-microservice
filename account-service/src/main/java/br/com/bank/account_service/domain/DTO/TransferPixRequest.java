package br.com.bank.account_service.domain.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TransferPixRequest(
        @NotBlank(message = "A chave pix de origem não pode está em branco") 
        String fromPix,
                
        @NotBlank(message = "A chave pix de destino não pode está em branco") 
        String toPix,
                
        @Positive(message = "O valor da transferência deve ser positivo")
        BigDecimal amount) {

}
