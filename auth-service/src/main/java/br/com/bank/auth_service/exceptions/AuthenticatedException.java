package br.com.bank.auth_service.exceptions;

public class AuthenticatedException extends RuntimeException {
    public AuthenticatedException(String message){
        super(message);
    }
}
