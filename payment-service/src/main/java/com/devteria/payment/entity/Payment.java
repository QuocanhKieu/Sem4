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
    private BigDecimal initialPrice;

    @Column(nullable = false)
    private BigDecimal finalPrice;

    @ManyToOne
    @JoinColumn(name = "voucher_id", nullable = true)
    private Voucher voucher;
    private BigDecimal voucherAmount; // Stores refunded amount, if any

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private String currency;

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

    // paypal
    private String paymentId; // PAYID-M7L7ZRY4HT17711CU354961N
    private String saleId; // 6X449338E3008973X (for refunds)
    private String payerId; // buyer paypal id
    private String buyerEmail; // sb-mt6aj38861978@personal.example.com
    private String merchantId; // receiver paypal user id
    private BigDecimal transactionFee; // 0.51 USD
    private String refundUrl; // URL for refund
    private String refundPaymentId; // URL for refund


    // Constructors, Getters, Setters
}
