package br.com.bank.investment_service.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bank.investment_service.domain.DTO.CreateInvestmentRequest;
import br.com.bank.investment_service.domain.DTO.DepositRequest;
import br.com.bank.investment_service.domain.DTO.InvestmentResponse;
import br.com.bank.investment_service.domain.DTO.TransferPixRequest;
import br.com.bank.investment_service.domain.DTO.WithdrawRequest;
import br.com.bank.investment_service.domain.model.InvestmentWallet;
import br.com.bank.investment_service.domain.repository.InvestmentRepository;
import br.com.bank.investment_service.exceptions.AccountNotFoundException;
import br.com.bank.investment_service.exceptions.AccountWithInvestmentException;
import br.com.bank.investment_service.exceptions.InvestmentNotFoundException;
import br.com.bank.investment_service.exceptions.NoFundsEnoughException;
import br.com.bank.investment_service.exceptions.UnauthorizatedAccessException;
import br.com.bank.investment_service.exceptions.UserNotFoundException;
import br.com.bank.investment_service.service.AccountService;
import br.com.bank.investment_service.service.InvestmentService;
import br.com.bank.investment_service.service.UserService;
import br.com.bank.investment_service.utils.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class InvestmentServiceImpl implements InvestmentService {
	private final InvestmentRepository investmentRepository;
	private final UserService userService;
	private final AccountService accountService;

	@Override
	public Flux<InvestmentResponse> getAllInvestmentsByUser(int page, int limit) {
		int offset = (page - 1) * limit;
		return SecurityUtil.getCurrentUserId()
				.flatMapMany(userId -> userService.getUserById(userId)
						.thenMany(investmentRepository.findAllByUserId(userId))
						.skip(offset)
						.take(limit)
						.map(this::toDTO));
	}

	@Override
	public Mono<InvestmentResponse> getInvestmentByPix(String pix) {
		return SecurityUtil.getCurrentUserId()
				.flatMap(userId -> userService.getUserById(userId)
						.then(investmentRepository.findByPix(pix))
						.switchIfEmpty(
								Mono.error(new InvestmentNotFoundException("Conta de investimento não encontrada.")))
						.flatMap(investment -> {
							if (!investment.getUserId().equals(userId)) {
								return Mono.error(new UnauthorizatedAccessException(
										"Essa conta não pertence ao usuário autenticado."));
							}
							return Mono.just(toDTO(investment));
						}));
	}

	@Override
	public Mono<InvestmentResponse> create(CreateInvestmentRequest request) {
		return SecurityUtil.getCurrentUserId()
				.flatMap(userId -> userService.getUserById(userId)
						.switchIfEmpty(Mono.error(new UserNotFoundException("Usuário não encontrado")))
						.then(investmentRepository.existsByPix(request.pix()))
						.flatMap(exists -> {
							if (exists) {
								return Mono.error(new AccountWithInvestmentException(
										"Já existe uma carteira de investimento com essa chave Pix."));
							}
							InvestmentWallet investment = new InvestmentWallet();
							investment.setPix(request.pix());
							investment.setTax(request.tax());
							investment.setInitialDeposit(request.amount());
							investment.setBalance(request.amount());
							investment.setUserId(userId);

							return investmentRepository.save(investment).thenReturn(toDTO(investment));
						}));
	}

	@Override
	@Transactional
	public Mono<InvestmentResponse> invest(TransferPixRequest request) {
		return SecurityUtil.getCurrentUserId()
				.flatMap(userId -> userService.getUserById(userId)
						.then(validateAccountExists(request.fromPix()))
						.then(investmentRepository.findByPix(request.toPix())
								.switchIfEmpty(Mono.error(
										new InvestmentNotFoundException("Carteira de investimento não encontrada.")))
								.flatMap(wallet -> {
									if (!wallet.getUserId().equals(userId)) {
										return Mono.error(new UnauthorizatedAccessException(
												"Você não tem permissão para investir nesta carteira."));
									}

									WithdrawRequest withdrawRequest = new WithdrawRequest(request.fromPix(),
											request.amount());

									return accountService.withdraw(withdrawRequest)
											.onErrorResume(ex -> {
												log.error("Erro ao sacar da conta corrente", ex);
												return Mono.error(
														new RuntimeException("Falha ao sacar da conta corrente"));
											})
											.flatMap(account -> {
												wallet.deposit(request.amount());
												return investmentRepository.save(wallet)
														.map(this::toDTO);
											});
								})));

	}

	@Override
	@Transactional
	public Mono<InvestmentResponse> withdraw(TransferPixRequest request) {
		return SecurityUtil.getCurrentUserId()
				.flatMap(userId -> userService.getUserById(userId)
						.then(validateAccountExists(request.toPix()))
						.then(investmentRepository.findByPix(request.fromPix())
								.switchIfEmpty(Mono.error(
										new InvestmentNotFoundException("Carteira de investimento não encontrada.")))
								.flatMap(wallet -> {
									if (!wallet.getUserId().equals(userId)) {
										return Mono.error(new UnauthorizatedAccessException(
												"Você não tem permissão para investir nesta carteira."));
									}

									if (wallet.getBalance().compareTo(request.amount()) < 0) {
										return Mono.error(new NoFundsEnoughException(
												"Saldo insuficiente na carteira de investimento."));
									}

									DepositRequest depositRequest = new DepositRequest(request.toPix(),
											request.amount());

									wallet.withdraw(request.amount());

									return investmentRepository.save(wallet)
											.then(accountService.deposit(depositRequest))
											.map(account -> toDTO(wallet));
								})));
	}

	@Override
	public Flux<InvestmentResponse> updateYield() {
		return SecurityUtil.getCurrentUserId()
				.flatMapMany(userId -> investmentRepository.findAllByUserId(userId))
				.map(wallet -> {
					wallet.updateYield();
					return wallet;
				})
				.flatMap(investmentRepository::save)
				.map(this::toDTO);
	}

	private InvestmentResponse toDTO(InvestmentWallet wallet) {
		return new InvestmentResponse(wallet.getId(), wallet.getPix(), wallet.getBalance(), wallet.getTax());
	}

	private Mono<Void> validateAccountExists(String pix) {
		return accountService.getAccountByPix(pix)
				.switchIfEmpty(Mono.error(new AccountNotFoundException("Conta corrente não encontrada.")))
				.then();
	}

}
