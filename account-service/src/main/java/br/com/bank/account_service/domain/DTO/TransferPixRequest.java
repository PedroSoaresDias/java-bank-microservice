package br.com.bank.account_service.domain.DTO;

import java.math.BigDecimal;

public record TransferPixRequest(String fromPix, String toPix, BigDecimal amount) {

}
