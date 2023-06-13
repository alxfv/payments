package com.example.riskengineservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewPaymentEvent {
    @Getter
    @Setter
    private String userId;

    @Getter
    @Setter
    private BigDecimal amount;

    @Getter
    @Setter
    private String currency;

    @Getter
    @Setter
    private String payeeId;

    @Getter
    @Setter
    private String paymentMethod;

    @Getter
    @Setter
    private String idempotencyKey;
}
