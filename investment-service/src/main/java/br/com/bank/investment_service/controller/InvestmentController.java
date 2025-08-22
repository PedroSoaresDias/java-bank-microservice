package br.com.bank.investment_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.bank.investment_service.domain.DTO.CreateInvestmentRequest;
import br.com.bank.investment_service.domain.DTO.InvestmentResponse;
import br.com.bank.investment_service.domain.DTO.TransferPixRequest;
import br.com.bank.investment_service.service.InvestmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/investments")
@AllArgsConstructor
public class InvestmentController {
    private final InvestmentService investmentService;

    @GetMapping
    public Flux<InvestmentResponse> getAllInvestments(@RequestParam(defaultValue = "1", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "limit") int limit) {
        return investmentService.getAllInvestmentsByUser(page, limit);
    }
    
    @GetMapping("/{pix}")
    public Mono<InvestmentResponse> getAccountByPix(@PathVariable("pix") String pix) {
        return investmentService.getInvestmentByPix(pix);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<InvestmentResponse> create(@Valid @RequestBody CreateInvestmentRequest request) {
        return investmentService.create(request);
    }

    @PostMapping("/invest")
    public Mono<InvestmentResponse> invest(@Valid @RequestBody TransferPixRequest request) {
        return investmentService.invest(request);
    }

    @PostMapping("/withdraw")
    public Mono<InvestmentResponse> withdraw(@Valid @RequestBody TransferPixRequest request) {
        return investmentService.withdraw(request);
    }

    @PostMapping("/yield/update")
    public Mono<InvestmentResponse> updateYield() {
        return investmentService.updateYield();
    }
}
