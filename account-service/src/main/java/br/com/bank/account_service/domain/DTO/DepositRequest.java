package br.com.bank.account_service.domain.DTO;

import java.math.BigDecimal;

public record DepositRequest(String pix, BigDecimal amount) {

}
