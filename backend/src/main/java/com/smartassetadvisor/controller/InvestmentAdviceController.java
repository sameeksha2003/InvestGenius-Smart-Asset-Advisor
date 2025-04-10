package com.smartassetadvisor.controller;

import com.smartassetadvisor.service.InvestmentAdviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investment")
public class InvestmentAdviceController {
    
    @Autowired
    private final InvestmentAdviceService investmentAdviceService;

    public InvestmentAdviceController(InvestmentAdviceService investmentAdviceService) {
        this.investmentAdviceService = investmentAdviceService;
    }

    @GetMapping("/advice")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> getAdvice(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ User not authenticated");
        }

        String email = userDetails.getUsername();
        return ResponseEntity.ok(investmentAdviceService.getInvestmentAdvice(email));
    }
}
