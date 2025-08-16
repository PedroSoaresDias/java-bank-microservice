package br.com.bank.auth_service.exceptions;

public class UnauthorizatedAccessException extends RuntimeException {
    public UnauthorizatedAccessException(String message){
        super(message);
    }
}
