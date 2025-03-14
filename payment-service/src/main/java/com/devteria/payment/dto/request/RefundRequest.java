package com.devteria.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {
    private String paymentId;
    private BigDecimal refundAmount;
    private String refundReason;
}


