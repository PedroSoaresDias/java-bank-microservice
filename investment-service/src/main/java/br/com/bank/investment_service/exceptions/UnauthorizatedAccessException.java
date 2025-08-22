package br.com.bank.investment_service.exceptions;

public class UnauthorizatedAccessException extends RuntimeException {
    public UnauthorizatedAccessException(String message){
        super(message);
    }
}
