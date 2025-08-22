package br.com.bank.investment_service.domain.DTO;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String error, String message, String path, LocalDateTime timestamp) {

}
