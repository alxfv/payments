package com.example.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {
    private static final String NEW_PAYMENTS_TOPIC = "new_payments";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaTemplate<String, NewPayment> kafkaTemplate;

    @Test
    void shouldCreatePayment() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID payeeId = UUID.randomUUID();
        UUID idempotencyKey = UUID.randomUUID();

        String jsonPayment = "{\n" +
                "\"userId\":\"" + userId + "\",\n" +
                "\"currency\":\"USD\",\n" +
                "\"amount\":100.0,\n" +
                "\"payeeId\":\"" + payeeId + "\",\n" +
                "\"paymentMethod\":\"CREDIT_CARD\",\n" +
                "\"idempotencyKey\":\"" + idempotencyKey + "\"\n" +
                "}";

        this.mockMvc.perform(post("/payments")
                        .contentType("application/json")
                        .content(jsonPayment))
                .andExpect(status().isOk());

        NewPayment expectedPayment = new NewPayment(
                userId,
                Currency.USD,
                BigDecimal.valueOf(100.0),
                payeeId,
                PaymentMethod.CREDIT_CARD,
                idempotencyKey
        );

        verify(kafkaTemplate).send(eq(NEW_PAYMENTS_TOPIC), eq(idempotencyKey.toString()), eq(expectedPayment));
    }

    @Test
    void paymentMethods() throws Exception {
        this.mockMvc.perform(get("/payment-methods"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"CREDIT_CARD\", \"PAYPAL\"]"));
    }
}
