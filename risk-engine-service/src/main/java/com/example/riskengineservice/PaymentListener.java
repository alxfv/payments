package com.example.riskengineservice;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class PaymentListener {
    public static final String PAYMENTS_TOPIC = "new_payments";
    private final Logger logger = LoggerFactory.getLogger(PaymentListener.class);

    private final PaymentRepository paymentRepository;

    @Autowired
    private final RiskEngine riskEngine;

    public PaymentListener(PaymentRepository paymentRepository, RiskEngine riskEngine) {
        this.paymentRepository = paymentRepository;
        this.riskEngine = riskEngine;
    }


    @KafkaListener(id = "risk_engine_group", topics = PAYMENTS_TOPIC)
    public void listen(NewPaymentEvent newPayment, Acknowledgment ack) {
        logger.info("Received from payments: " + newPayment);

        Payment payment = createPaymentFromEvent(newPayment);
        payment.setRiskScore(riskEngine.generateRiskScore(payment));

        try {
            paymentRepository.save(payment);
            logger.info("Saved a new payment: " + payment);
        } catch (DataIntegrityViolationException e) {
            logger.info("Payment already in the database: " + e);
        }
        ack.acknowledge();
    }

    private static Payment createPaymentFromEvent(NewPaymentEvent newPayment) {
        Payment payment = new Payment();

        payment.setAmount(newPayment.getAmount());
        payment.setUserId(newPayment.getUserId());
        payment.setPayeeId(newPayment.getPayeeId());
        payment.setIdempotencyKey(newPayment.getIdempotencyKey());
        payment.setPaymentMethod(newPayment.getPaymentMethod());
        payment.setCurrency(newPayment.getCurrency());

        return payment;
    }


    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(PAYMENTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
