package br.com.bank.investment_service.service;

import br.com.bank.investment_service.domain.DTO.AccountDTO;
import br.com.bank.investment_service.domain.DTO.DepositRequest;
import br.com.bank.investment_service.domain.DTO.WithdrawRequest;
import reactor.core.publisher.Mono;

public interface AccountService {
    Mono<AccountDTO> getAccountByPix(String pix);

    Mono<AccountDTO> deposit(DepositRequest request);

    Mono<AccountDTO> withdraw(WithdrawRequest request);
}
