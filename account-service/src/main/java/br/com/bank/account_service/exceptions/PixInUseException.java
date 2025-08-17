package br.com.bank.account_service.exceptions;

public class PixInUseException extends RuntimeException {
    public PixInUseException(String message) {
        super(message);
    }
}
