package com.devteria.payment.controller;


import com.devteria.payment.configuration.SecurityUtils;
import com.devteria.payment.dto.VoucherDTO;
import com.devteria.payment.entity.Voucher;
import com.devteria.payment.service.PaymentService;
import com.devteria.payment.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")

public class PaymentController {
    private final PaymentService paymentService;
    private final SecurityUtils securityUtils;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestParam String orderId,
            @RequestParam String bookingId,
            @RequestParam(required = false) String voucherId) {

        String userId = securityUtils.getCurrentUserId(); // Replace with actual user retrieval logic

        String message = paymentService.verifyPayment(orderId, bookingId, voucherId, userId);

        if (message.contains("Failed")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }

        return ResponseEntity.ok(message);
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refundOrder(@RequestParam String transactionId, @RequestParam BigDecimal refundAmount) {
        try {
            String message = paymentService.processRefund(transactionId, refundAmount);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
