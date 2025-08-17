package br.com.bank.account_service.exceptions;

public class UnauthorizatedAccessException extends RuntimeException {
    public UnauthorizatedAccessException(String message){
        super(message);
    }
}
