package br.com.bank.investment_service.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.bank.investment_service.adapter.HttpClientAdapter;
import br.com.bank.investment_service.domain.DTO.AccountDTO;
import br.com.bank.investment_service.domain.DTO.DepositRequest;
import br.com.bank.investment_service.domain.DTO.WithdrawRequest;
import br.com.bank.investment_service.service.AccountService;
import reactor.core.publisher.Mono;

@Service
public class AccountServiceImpl implements AccountService {
    private String accountServiceUrl;
    private final HttpClientAdapter httpClient;

    public AccountServiceImpl(@Value("${account.service.url}") String accountServiceUrl, HttpClientAdapter httpClient) {
        this.accountServiceUrl = accountServiceUrl;
        this.httpClient = httpClient;
    }

    @Override
    public Mono<AccountDTO> getAccountByPix(String pix) {
        String url = accountServiceUrl + "/accounts/" + pix;
        return httpClient.get(url, AccountDTO.class);
    }

    @Override
    public Mono<AccountDTO> deposit(DepositRequest request) {
        String url = accountServiceUrl + "/accounts/deposit";
        return httpClient.post(url, request, AccountDTO.class);
    }

    @Override
    public Mono<AccountDTO> withdraw(WithdrawRequest request) {
        String url = accountServiceUrl + "/accounts/withdraw";
        return httpClient.post(url, request, AccountDTO.class);
    }

}
