package com.example.paymentservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewPayment {
    @Getter
    @NotNull
    private UUID userId;

    @Getter
    @NotNull
    private Currency currency;

    @Getter
    @NotNull
    private BigDecimal amount;

    @Getter
    @NotNull
    private UUID payeeId;

    @Getter
    @NotNull
    private PaymentMethod paymentMethod;

    @Getter
    @NotNull
    private UUID idempotencyKey;
}
