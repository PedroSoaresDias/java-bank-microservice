package br.com.bank.account_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.bank.account_service.domain.DTO.AccountResponse;
import br.com.bank.account_service.domain.DTO.CreateAccountRequest;
import br.com.bank.account_service.domain.DTO.DepositRequest;
import br.com.bank.account_service.domain.DTO.TransferPixRequest;
import br.com.bank.account_service.domain.DTO.WithdrawRequest;
import br.com.bank.account_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    public Flux<AccountResponse> getAllAccounts(@RequestParam(defaultValue = "1", name = "page") int page, @RequestParam(defaultValue = "10", name = "limit") int limit) {
        return accountService.getAllMyAccounts(page, limit);
    }

    @GetMapping("/{pix}")
    public Mono<AccountResponse> getAccountByPix(@PathVariable("pix") String pix) {
        return accountService.getAccountByPix(pix);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @PostMapping("/deposit")
    public Mono<AccountResponse> deposit(@Valid @RequestBody DepositRequest request) {
        return accountService.deposit(request);
    }

    @PostMapping("/withdraw")
    public Mono<AccountResponse> withdraw(@Valid @RequestBody WithdrawRequest request) {
        return accountService.withdraw(request);
    }

    @PostMapping("/transfer")
    public Mono<AccountResponse> transfer(@Valid @RequestBody TransferPixRequest request) {
        return accountService.transfer(request);
    }
}
