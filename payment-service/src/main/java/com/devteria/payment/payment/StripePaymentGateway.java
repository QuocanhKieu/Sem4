package com.devteria.payment.payment;

import com.devteria.payment.dto.request.PaymentRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class StripePaymentGateway implements PaymentGateway {

    @Override
    public String charge(PaymentRequest request) {
        // Simulate calling Stripe API
        return "stripe_txn_" + UUID.randomUUID();
    }

    @Override
    public boolean refund(String transactionReference, BigDecimal amount) {
        // Simulate Stripe refund API
        return true;
    }
}