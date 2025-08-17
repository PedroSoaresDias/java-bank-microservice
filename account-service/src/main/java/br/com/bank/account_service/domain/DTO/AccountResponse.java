package br.com.bank.account_service.domain.DTO;

import java.math.BigDecimal;

public record AccountResponse(Long id, String pix, BigDecimal balance) {

}
