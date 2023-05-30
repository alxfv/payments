package com.example.paymentservice;


import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
public class PaymentController {
    private static final String NEW_PAYMENTS_TOPIC = "new_payments";
    private final KafkaTemplate<String, NewPayment> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);


    public PaymentController(KafkaTemplate<String, NewPayment> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @PostMapping("/payments")
    public NewPayment createPayment(@Valid @RequestBody NewPayment payment) {
        logger.info("Payment: " + payment);

        // use idempotency key as a partition key
        String key = payment.getIdempotencyKey().toString();
        kafkaTemplate.send(NEW_PAYMENTS_TOPIC, key, payment);

        return payment;
    }

    @GetMapping("/payment-methods")
    Iterable<String> paymentMethods() {
        return Arrays.stream(PaymentMethod.values())
                .map(PaymentMethod::name)
                .collect(Collectors.toList());
    }
}
