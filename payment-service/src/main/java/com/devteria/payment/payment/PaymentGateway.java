package com.devteria.payment.payment;

import com.devteria.payment.dto.request.PaymentRequest;

import java.math.BigDecimal;

public interface PaymentGateway {
    String charge(PaymentRequest request);
    boolean refund(String transactionReference, BigDecimal amount);
}