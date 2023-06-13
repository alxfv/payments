package com.example.riskengineservice;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RiskEngine {
    private final Random random = new Random();

    public int generateRiskScore(Payment payment) {
        // Generate a random number between 0 and 100
        return random.nextInt(101);
    }
}