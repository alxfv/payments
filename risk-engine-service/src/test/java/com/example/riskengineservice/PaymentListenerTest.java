package com.example.riskengineservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PaymentListenerTest {

    @MockBean
    private PaymentRepository paymentRepository;

    @Mock
    private NewPaymentEvent newPaymentEvent;

    @Mock
    private Acknowledgment ack;

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
        paymentListener.listen(newPaymentEvent, ack);

        Mockito.verify(paymentRepository, Mockito.times(1)).save(Mockito.any(Payment.class));

        Mockito.verify(ack, Mockito.times(1)).acknowledge();
    }

    @Test
    void shouldNotAcknowledgeWhenSaveFailsWithUnexpectedException() {
        // Arrange
        PaymentRepository mockPaymentRepository = Mockito.mock(PaymentRepository.class);
        Mockito.when(mockPaymentRepository.save(Mockito.any(Payment.class))).thenThrow(IllegalArgumentException.class);

        Acknowledgment mockAck = Mockito.mock(Acknowledgment.class);
        RiskEngine mockRiskEngine = Mockito.mock(RiskEngine.class);

        PaymentListener paymentListener = new PaymentListener(mockPaymentRepository, mockRiskEngine);

        // Act
        try {
            paymentListener.listen(newPaymentEvent, mockAck);
        } catch (IllegalArgumentException ex) {
            // Expected exception
        }

        // Assert
        Mockito.verify(mockAck, Mockito.times(0)).acknowledge();
    }
}
