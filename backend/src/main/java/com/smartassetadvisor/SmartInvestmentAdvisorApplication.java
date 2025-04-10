package com.smartassetadvisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.smartassetadvisor")
public class SmartInvestmentAdvisorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartInvestmentAdvisorApplication.class, args);
    }
}
