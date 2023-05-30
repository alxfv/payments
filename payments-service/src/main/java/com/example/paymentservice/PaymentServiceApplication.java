package com.example.paymentservice;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example"})
public class PaymentServiceApplication {

    public static final String PAYMENTS_TOPIC = "new_payments";

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(PAYMENTS_TOPIC)
                .partitions(2)
                .replicas(1)
                .build();
    }
}

