package com.example.riskengineservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PaymentListenerTest {

    @MockBean
    private PaymentRepository paymentRepository;

    @Mock
    private NewPaymentEvent newPaymentEvent;

    @Autowired
    private PaymentListener paymentListener;

    @BeforeEach
    void setUp() {
        Mockito.when(newPaymentEvent.getAmount()).thenReturn(BigDecimal.ONE);
        Mockito.when(newPaymentEvent.getUserId()).thenReturn("user_id");
        Mockito.when(newPaymentEvent.getPayeeId()).thenReturn("payee_id");
        Mockito.when(newPaymentEvent.getIdempotencyKey()).thenReturn("idempotency_key");
        Mockito.when(newPaymentEvent.getPaymentMethod()).thenReturn("payment_method");
        Mockito.when(newPaymentEvent.getCurrency()).thenReturn("USD");
    }

    @Test
    void listen() {
        paymentListener.listen(newPaymentEvent);

        Mockito.verify(paymentRepository, Mockito.times(1)).save(Mockito.any(Payment.class));
    }
}
