package com.example.riskengineservice;

import java.util.Random;

public class RiskEngine {
    private final Random random = new Random();

    public int generateRiskScore(Payment payment) {
        // Generate a random number between 0 and 100
        return random.nextInt(101);
    }
}