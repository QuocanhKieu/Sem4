package com.devteria.payment.dto.request;

import com.devteria.payment.constant.PaymentGatewayEnums;
import com.devteria.payment.constant.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String bookingId;
    private String orderId;
    private String voucherId;
    private BigDecimal initialPrice;
//    private PaymentMethod paymentMethod;
//    private PaymentGatewayEnums paymentGateway;
}


//@RequestParam String orderId,
//@RequestParam String bookingId,
//@RequestParam(required = false) String voucherId) {


