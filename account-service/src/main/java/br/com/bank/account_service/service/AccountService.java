package br.com.bank.account_service.service;

import br.com.bank.account_service.domain.DTO.AccountResponse;
import br.com.bank.account_service.domain.DTO.CreateAccountRequest;
import br.com.bank.account_service.domain.DTO.DepositRequest;
import br.com.bank.account_service.domain.DTO.TransferPixRequest;
import br.com.bank.account_service.domain.DTO.WithdrawRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {
    Flux<AccountResponse> getAllMyAccounts();

    Mono<AccountResponse> getAccountByPix(String pix);
    
    Mono<Void> createAccount(CreateAccountRequest request);

    Mono<Void> deposit(DepositRequest request);

    Mono<Void> withdraw(WithdrawRequest request);

    Mono<Void> transfer(TransferPixRequest request);
}
