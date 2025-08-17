package br.com.bank.account_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.bank.account_service.domain.DTO.AccountResponse;
import br.com.bank.account_service.domain.DTO.CreateAccountRequest;
import br.com.bank.account_service.domain.DTO.DepositRequest;
import br.com.bank.account_service.domain.DTO.TransferPixRequest;
import br.com.bank.account_service.domain.DTO.WithdrawRequest;
import br.com.bank.account_service.service.AccountService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public Flux<AccountResponse> getAllAccounts() {
        return accountService.getAllMyAccounts();
    }

    @GetMapping("/{pix}")
    public Mono<AccountResponse> getAccountByPix(@PathVariable("pix") String pix) {
        return accountService.getAccountByPix(pix);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> createAccount(@RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deposit(@RequestBody DepositRequest request) {
        return accountService.deposit(request);
    }

    @PostMapping("/withdraw")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> withdraw(@RequestBody WithdrawRequest request) {
        return accountService.withdraw(request);
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> transfer(@RequestBody TransferPixRequest request) {
        return accountService.transfer(request);
    }
}
