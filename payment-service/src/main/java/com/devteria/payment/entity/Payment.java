package com.devteria.payment.entity;
import java.math.BigDecimal;
import java.time.Instant;

import com.devteria.payment.constant.PaymentGatewayEnums;
import com.devteria.payment.constant.PaymentMethod;
import com.devteria.payment.constant.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JoinColumn(name = "booking_id", nullable = false)
    private String bookingId;

    @JoinColumn(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "voucher_id", nullable = true)
    private Voucher voucher;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private String currency;

//    @Column(nullable = false)
//    @Enumerated(EnumType.STRING)
//    private PaymentGatewayEnums paymentGateway;

    @Column(nullable = false)
    private Instant paymentDate;

    // Refund-related fields
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus; // Default is paid


    @Column(precision = 10, scale = 2)
    private BigDecimal refundAmount; // Stores refunded amount, if any

    private Instant refundDate; // When the refund was processed

    @Column(length = 255)
    private String refundReason; // Optional reason for refund

    private String transactionReference; // Reference for external payment gateways

    // Constructors, Getters, Setters
}
