package com.smartassetadvisor.dto;

public class AuthRequest {
    private String name;
    private String email;
    private String password;
    private Double annualIncome;  // 🔥 FIXED: Changed from String to Double
    private int age;
    private String occupation;
    private String riskCategory;
    private String investmentGoals;

    public AuthRequest() {}

    public AuthRequest(String name, String email, String password, Double annualIncome, int age, 
                       String occupation, String riskCategory, String investmentGoals) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.annualIncome = annualIncome;
        this.age = age;
        this.occupation = occupation;
        this.riskCategory = riskCategory;
        this.investmentGoals = investmentGoals;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Double getAnnualIncome() { return annualIncome; }  // 🔥 FIXED
    public void setAnnualIncome(Double annualIncome) { this.annualIncome = annualIncome; } // 🔥 FIXED

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getRiskCategory() { return riskCategory; }
    public void setRiskCategory(String riskCategory) { this.riskCategory = riskCategory; }

    public String getInvestmentGoals() { return investmentGoals; }
    public void setInvestmentGoals(String investmentGoals) { this.investmentGoals = investmentGoals; }
}
