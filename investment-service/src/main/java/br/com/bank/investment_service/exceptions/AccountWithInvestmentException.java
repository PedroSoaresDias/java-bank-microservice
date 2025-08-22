package br.com.bank.investment_service.exceptions;

public class AccountWithInvestmentException extends RuntimeException {
    public AccountWithInvestmentException(String message) {
        super(message);
    }
}
