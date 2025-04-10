package com.smartassetadvisor.controller;

import com.smartassetadvisor.service.InvestmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investment")
public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @GetMapping("/recommendations")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> getInvestmentRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().body("❌ User not authenticated.");
        }

        String email = userDetails.getUsername();
        return ResponseEntity.ok(investmentService.getInvestmentRecommendations(email));
    }
}
