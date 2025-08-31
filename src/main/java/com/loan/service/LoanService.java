package com.loan.service;

import com.loan.entity.Loan;
import com.loan.repo.LoanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;


@Service
public class LoanService {


    @Autowired
    LoanRepo loanRepo;

    @Autowired
    private WebClient webClient;
    public Mono<String> loanRequest(Integer userId, Integer loanAmount) {
        Loan loan = new Loan();
        loan.setAmount(loanAmount);
        loan.setUserId(userId);
        loan.setCurrentStatus(Loan.CurrentStatus.PENDING);
        loan.setStatusMessage("Loan approval is in pending state");

        return loanRepo.save(loan) // save as pending
                .flatMap(savedLoan ->
                        webClient.post()
                                .uri("/bank/validate")
                                .bodyValue(Map.of("userId", userId, "loanAmount", loanAmount))
                                .retrieve()
                                .toBodilessEntity()
                                .flatMap(response -> {
                                    savedLoan.setCurrentStatus(Loan.CurrentStatus.APPROVED);
                                    savedLoan.setStatusMessage("Loan has been approved successfully based on bank balance validation.");
                                    return loanRepo.save(savedLoan)
                                            .thenReturn("Loan has been approved successfully!");
                                })
                                .onErrorResume(WebClientResponseException.class, ex -> {
                                    savedLoan.setCurrentStatus(Loan.CurrentStatus.REJECTED);
                                    savedLoan.setStatusMessage("Loan rejected: " + ex.getResponseBodyAsString());
                                    return loanRepo.save(savedLoan)
                                            .then(Mono.error(new RuntimeException("Loan request denied: " + ex.getResponseBodyAsString())));
                                })
                                .onErrorResume(ex -> {
                                    savedLoan.setCurrentStatus(Loan.CurrentStatus.REJECTED);
                                    savedLoan.setStatusMessage("Loan rejected due to internal error");
                                    return loanRepo.save(savedLoan)
                                            .then(Mono.error(new RuntimeException("Internal error during loan processing")));
                                })
                );
    }



}
