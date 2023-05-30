package com.example.riskengineservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
class PaymentController {
    @Autowired
    private PaymentRepository repository;

    @GetMapping("/payments")
    Iterable<Payment> all() {
        return this.repository.findAll();
    }
}
