package br.com.bank.account_service.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import br.com.bank.account_service.domain.DTO.AccountResponse;
import br.com.bank.account_service.domain.DTO.CreateAccountRequest;
import br.com.bank.account_service.domain.DTO.DepositRequest;
import br.com.bank.account_service.domain.DTO.TransferPixRequest;
import br.com.bank.account_service.domain.DTO.WithdrawRequest;
import br.com.bank.account_service.domain.model.AccountWallet;
import br.com.bank.account_service.domain.repository.AccountRepository;
import br.com.bank.account_service.exceptions.AccountNotFoundException;
import br.com.bank.account_service.exceptions.NoFundsEnoughException;
import br.com.bank.account_service.exceptions.PixInUseException;
import br.com.bank.account_service.exceptions.UnauthorizatedAccessException;
import br.com.bank.account_service.exceptions.UserNotFoundException;
import br.com.bank.account_service.service.AccountService;
import br.com.bank.account_service.service.UserService;
import br.com.bank.account_service.utils.SecurityUtil;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;

    @Override
    public Flux<AccountResponse> getAllMyAccounts() {
        return SecurityUtil.getCurrentUserId()
                .flatMapMany(userId -> userService.getUserById(userId)
                        .thenMany(accountRepository.findAllByUserId(userId))
                        .map(this::toDTO));
    }

    @Override
    public Mono<AccountResponse> getAccountByPix(String pix) {
        return SecurityUtil.getCurrentUserId()
                .flatMap(userId -> userService.getUserById(userId)
                        .then(accountRepository.findByPix(pix))
                        .switchIfEmpty(Mono.error(new AccountNotFoundException("Conta não encontrada.")))
                        .map(this::toDTO));
    }

    @Override
    public Mono<Void> createAccount(CreateAccountRequest request) {
        return SecurityUtil.getCurrentUserId()
                .flatMap(userId -> userService.getUserById(userId)
                        .switchIfEmpty(Mono.error(new UserNotFoundException("Usuário não encontrado")))
                        .then(accountRepository.existsByPix(request.pix()))
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new PixInUseException("Pix já está em uso"));
                            }
                            AccountWallet account = new AccountWallet();
                            account.setPix(request.pix());
                            account.setBalance(request.balance() != null ? request.balance() : BigDecimal.ZERO);
                            account.setUserId(userId);

                            return accountRepository.save(account).then();
                        }));
    }

    @Override
    public Mono<Void> deposit(DepositRequest request) {
        return SecurityUtil.getCurrentUserId()
                .flatMap(userId -> accountRepository.findByPix(request.pix())
                        .flatMap(wallet -> {
                            if (!wallet.getUserId().equals(userId)) {
                                return Mono.error(new UnauthorizatedAccessException("Acesso não permitido"));
                            }
                            wallet.deposit(request.amount());
                            return accountRepository.save(wallet).then();
                        }));
    }

    @Override
    public Mono<Void> withdraw(WithdrawRequest request) {
        return SecurityUtil.getCurrentUserId()
                .flatMap(userId -> accountRepository.findByPix(request.pix())
                        .flatMap(wallet -> {
                            if (!wallet.getUserId().equals(userId)) {
                                return Mono.error(new UnauthorizatedAccessException("Acesso não permitido"));
                            }
                            if (wallet.getBalance().compareTo(request.amount()) < 0) {
                                return Mono.error(new NoFundsEnoughException("Saldo insuficiente"));
                            }
                            wallet.withdraw(request.amount());
                            return accountRepository.save(wallet).then();
                        }));
    }

    @Override
    public Mono<Void> transfer(TransferPixRequest request) {
        return SecurityUtil.getCurrentUserId()
                .flatMap(userId -> accountRepository.findByPix(request.fromPix())
                        .flatMap(fromWallet -> {
                            if (!fromWallet.getUserId().equals(userId)) {
                                return Mono.error(new UnauthorizatedAccessException("Acesso não permitido"));
                            }
                            if (fromWallet.getBalance().compareTo(request.amount()) < 0) {
                                return Mono.error(new NoFundsEnoughException("Saldo insuficiente"));
                            }

                            return accountRepository.findByPix(request.toPix())
                                    .flatMap(toWallet -> {
                                        fromWallet.withdraw(request.amount());
                                        toWallet.deposit(request.amount());

                                        return accountRepository.save(fromWallet)
                                                .then(accountRepository.save(toWallet))
                                                .then();
                                    });
                        }));
    }

    private AccountResponse toDTO(AccountWallet wallet) {
        return new AccountResponse(wallet.getId(), wallet.getPix(), wallet.getBalance());
    }

}
