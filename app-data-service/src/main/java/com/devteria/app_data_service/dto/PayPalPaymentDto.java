package com.devteria.app_data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayPalPaymentDto {
    private String paymentId; // PAYID-M7L7ZRY4HT17711CU354961N
    private String saleId; // 6X449338E3008973X (for refunds)
    private String payerId; // buyer paypal id
    private String buyerEmail; // sb-mt6aj38861978@personal.example.com
    private String merchantId; // receiver paypal user id
    private BigDecimal totalAmount; // 0.60 USD
    private BigDecimal transactionFee; // 0.51 USD
    private String currency; // USD
    private String refundUrl; // URL for refund
    private Instant transactionTime; // 2025-03-17T10:43:36Z
}

