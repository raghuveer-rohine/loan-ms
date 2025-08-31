package com.loan.controller;


import com.loan.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/loan")
public class LoanController {

    @Autowired
    LoanService loanService;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        String ipAddress = "unknown";
        try {
            ipAddress = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            // Log or handle exception if needed
            ipAddress = "Error getting IP: " + e.getMessage();
        }

        String message = "Loan service is up and running on IP: " + ipAddress;
        return ResponseEntity.ok(message);
    }


    @PostMapping("/request")
    public Mono<ResponseEntity<String>> loanRequest(@RequestParam Integer userId,
                                                    @RequestParam Integer loanAmount) {
        return loanService.loanRequest(userId, loanAmount)
                .map(result -> ResponseEntity.ok(result))
                .onErrorResume(ex -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Validation failed: " + ex.getMessage())));
    }


}
