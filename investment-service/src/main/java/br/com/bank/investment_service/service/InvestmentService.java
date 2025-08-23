package br.com.bank.investment_service.service;

import br.com.bank.investment_service.domain.DTO.CreateInvestmentRequest;
import br.com.bank.investment_service.domain.DTO.InvestmentResponse;
import br.com.bank.investment_service.domain.DTO.TransferPixRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InvestmentService {
    Flux<InvestmentResponse> getAllInvestmentsByUser(int page, int limit);

    Mono<InvestmentResponse> getInvestmentByPix(String pix);

    Mono<InvestmentResponse> create(CreateInvestmentRequest request);

    Mono<InvestmentResponse> invest(TransferPixRequest request);

    Mono<InvestmentResponse> withdraw(TransferPixRequest request);

    Flux<InvestmentResponse> updateYield();
}
