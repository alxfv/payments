package com.example.riskengineservice;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@ToString
@EqualsAndHashCode
@Entity
class Payment implements Serializable {
    @Id
    @GeneratedValue
    @Getter
    private UUID paymentId;

    @Column(nullable = false)
    @Getter
    @Setter
    private String userId;

    @Column(nullable = false)
    @Getter
    @Setter
    private String currency;

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal amount;

    @Column(nullable = false)
    @Getter
    @Setter
    private String payeeId;

    @Column(nullable = false)
    @Getter
    @Setter
    private String paymentMethod;

    @Column(nullable = false, unique = true)
    @Getter
    @Setter
    private String idempotencyKey;

    @Column(nullable = false)
    @Getter
    @Setter
    private Integer riskScore;
}
