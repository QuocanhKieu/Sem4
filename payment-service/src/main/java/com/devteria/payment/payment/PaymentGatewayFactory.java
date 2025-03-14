package com.devteria.payment.payment;
import com.devteria.payment.constant.PaymentGatewayEnums;

import org.springframework.stereotype.Component;

@Component
public class PaymentGatewayFactory {

    private final StripePaymentGateway stripeGateway;
    private final PayPalPaymentGateway paypalGateway;

    public PaymentGatewayFactory(StripePaymentGateway stripeGateway, PayPalPaymentGateway paypalGateway) {
        this.stripeGateway = stripeGateway;
        this.paypalGateway = paypalGateway;
    }

    public PaymentGateway getPaymentGateway(PaymentGatewayEnums method) {
        return switch (method) {
            case STRIPE -> stripeGateway;
            case PAYPAL -> paypalGateway;
            default -> throw new IllegalArgumentException("Unsupported payment method: " + method);
        };
    }
}