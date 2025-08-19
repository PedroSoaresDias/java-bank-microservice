package br.com.bank.account_service.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;

    @Override
    public Flux<AccountResponse> getAllMyAccounts(int page, int limit) {
        int offset = (page - 1) * limit;
        return SecurityUtil.getCurrentUserId()
                .flatMapMany(userId -> userService.getUserById(userId)
                        .thenMany(accountRepository.findAllByUserId(userId))
                        .skip(offset)
                        .take(limit)
                        .map(this::toDTO));
    }

    @Override
    public Mono<AccountResponse> getAccountByPix(String pix) {
        return SecurityUtil.getCurrentUserId()
                .flatMap(userId -> userService.getUserById(userId)
                        .then(accountRepository.findByPix(pix))
                        .switchIfEmpty(Mono.error(new AccountNotFoundException("Conta não encontrada.")))
                        .flatMap(account -> {
                            if (!account.getUserId().equals(userId)) {
                                return Mono.error(new UnauthorizatedAccessException(
                                        "Essa conta não pertence ao usuário autenticado."));
                            }
                            return Mono.just(toDTO(account));
                        }));

    }

    @Override
    public Mono<AccountResponse> createAccount(CreateAccountRequest request) {
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

                            return accountRepository.save(account).thenReturn(toDTO(account));
                        }));
    }

    @Override
    @Transactional
    public Mono<AccountResponse> deposit(DepositRequest request) {
        return SecurityUtil.getCurrentUserId()
                .flatMap(userId -> accountRepository.findByPix(request.pix())
                        .switchIfEmpty(Mono.error(new AccountNotFoundException("Conta não encontrada")))
                        .flatMap(wallet -> {
                            if (!wallet.getUserId().equals(userId)) {
                                return Mono.error(new UnauthorizatedAccessException("Você não tem permissão para depositar nessa conta"));
                            }
                            wallet.deposit(request.amount());
                            log.info("Usuário {} realizou depósito de {} na conta {}", userId, request.amount(),
                                    request.pix());
                            return accountRepository.save(wallet).thenReturn(toDTO(wallet));

                        }));
    }

    @Override
    @Transactional
    public Mono<AccountResponse> withdraw(WithdrawRequest request) {
        return SecurityUtil.getCurrentUserId()
                .flatMap(userId -> accountRepository.findByPix(request.pix())
                        .switchIfEmpty(Mono.error(new AccountNotFoundException("Conta não encontrada")))
                        .flatMap(wallet -> {
                            if (!wallet.getUserId().equals(userId)) {
                                return Mono.error(new UnauthorizatedAccessException("Você não tem permissão para saquar nesta conta"));
                            }
                            if (wallet.getBalance().compareTo(request.amount()) < 0) {
                                return Mono.error(new NoFundsEnoughException("Saldo insuficiente"));
                            }
                            wallet.withdraw(request.amount());
                            log.info("Usuário {} realizou saque de {} na conta {}", userId, request.amount(),
                                    request.pix());
                            return accountRepository.save(wallet).thenReturn(toDTO(wallet));
                        }));
    }

    @Override
    @Transactional
    public Mono<AccountResponse> transfer(TransferPixRequest request) {
        return SecurityUtil.getCurrentUserId()
                .flatMap(userId -> accountRepository.findByPix(request.fromPix())
                        .switchIfEmpty(Mono.error(new AccountNotFoundException("Conta de origem não encontrada")))
                        .flatMap(fromWallet -> {
                            if (!fromWallet.getUserId().equals(userId)) {
                                return Mono.error(new UnauthorizatedAccessException("Você não tem permissão para fazer transferência desta conta para outra"));
                            }
                            if (fromWallet.getBalance().compareTo(request.amount()) < 0) {
                                return Mono.error(new NoFundsEnoughException("Saldo insuficiente"));
                            }

                            return accountRepository.findByPix(request.toPix())
                                    .switchIfEmpty(Mono.error(new AccountNotFoundException("Conta de destino não encontrada")))
                                    .flatMap(toWallet -> {
                                        fromWallet.withdraw(request.amount());
                                        toWallet.deposit(request.amount());

                                        log.info("Usuário {} transferiu {} da conta {} para a conta {}", userId,
                                                request.amount(), request.fromPix(), request.toPix());

                                        return accountRepository.save(fromWallet)
                                                .then(accountRepository.save(toWallet))
                                                .thenReturn(toDTO(fromWallet));
                                    });
                        }));
    }

    private AccountResponse toDTO(AccountWallet wallet) {
        return new AccountResponse(wallet.getId(), wallet.getPix(), wallet.getBalance());
    }

}
