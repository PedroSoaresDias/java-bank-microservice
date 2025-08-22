package br.com.bank.investment_service.domain.DTO;

import java.math.BigDecimal;

public record AccountDTO(Long id, String pix, BigDecimal balance) {

}
