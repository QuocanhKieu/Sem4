package com.devteria.payment.service;

import com.devteria.payment.constant.PaymentMethod;
import com.devteria.payment.constant.PaymentStatus;
import com.devteria.payment.dto.request.ConfirmedBookingRequest;
import com.devteria.payment.dto.request.PaymentRequest;
import com.devteria.payment.dto.request.RefundRequest;
import com.devteria.payment.entity.Payment;
import com.devteria.payment.entity.Voucher;
import com.devteria.payment.exception.AppException;
import com.devteria.payment.exception.ErrorCode;
import com.devteria.payment.payment.PaymentGateway;
import com.devteria.payment.payment.PaymentGatewayFactory;
import com.devteria.payment.repository.PaymentRepository;
import com.devteria.payment.repository.VoucherRepository;
import com.devteria.payment.repository.httpclient.BookingAppDataClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PayPalService payPalService;
    //    private final BookingRepository bookingRepository;
    private final VoucherRepository voucherRepository;
    private final PaymentGatewayFactory paymentGatewayFactory;
    private final BookingAppDataClient bookingAppDataClient;
    @Value("${paypal.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public String processRefund(String transactionId, BigDecimal refundAmount) {
        Payment payment = paymentRepository.findByTransactionReference(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // 🛑 Check if already refunded
        if (!payment.getPaymentStatus().equals(PaymentStatus.PAID)) {
            throw new RuntimeException("Payment already refunded");
        }

// 🕒 Validate refund period (e.g., within 7 days)
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        if (payment.getPaymentDate().isBefore(sevenDaysAgo)) {
            throw new RuntimeException("Refund period expired (7 days)");
        }


        // 💰 Call PayPal refund API
        String refundTransactionId = payPalService.refundPayment(transactionId, refundAmount, payment.getCurrency());

        // ✅ Update payment record
        payment.setPaymentStatus(
                refundAmount.compareTo(payment.getTotalPrice()) == 0 ? PaymentStatus.REFUNDED : PaymentStatus.PARTIALLY_REFUNDED
        );
        payment.setRefundDate(Instant.now());
        payment.setTransactionReference(refundTransactionId);
        paymentRepository.save(payment);

        return "Refund processed successfully. Refund ID: " + refundTransactionId;
    }


    public String verifyPayment(String orderId, String bookingId, String voucherId, String userId) {
        String accessToken = payPalService.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/v2/checkout/orders/" + orderId,
                HttpMethod.GET,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonResponse = new JSONObject(response.getBody());

            String status = jsonResponse.getString("status");
            String transactionId = jsonResponse.getString("id");
            String currency = jsonResponse.getJSONArray("purchase_units")
                    .getJSONObject(0).getJSONObject("amount").getString("currency_code");
            BigDecimal amount = new BigDecimal(jsonResponse.getJSONArray("purchase_units")
                    .getJSONObject(0).getJSONObject("amount").getString("value"));


            Voucher voucher = voucherRepository.findById(voucherId)
                    .orElseThrow(() -> new RuntimeException("Voucher not found"));

            if ("COMPLETED".equals(status)) {
                // 🟢 Store the payment record
                Payment payment = new Payment();
                payment.setTransactionReference(transactionId);
                payment.setPaymentStatus(PaymentStatus.PAID);
                payment.setTotalPrice(amount);
                payment.setCurrency(currency);
                payment.setUserId(userId);
                payment.setBookingId(bookingId);
                payment.setVoucher(voucher);
                payment.setPaymentMethod(PaymentMethod.PAYPAL);
                payment.setPaymentDate(Instant.now());

                paymentRepository.save(payment);


                try {
                    bookingAppDataClient.confirmBooking(ConfirmedBookingRequest.builder()
                            .bookingId(bookingId)
                            .finalPrice(amount.doubleValue())
                            .voucherId(voucherId)
                            .voucherAmount(voucher.getDiscountAmount().doubleValue())
                            .build());

                } catch (FeignException e) { // Handle Feign client errors
                    throw new AppException(ErrorCode.CANNOT_CONFIRM_BOOKING);
                }



                return "Payment Verified and Stored Successfully!";
            }
        }

        return "Payment Verification Failed!";
    }

    //    public Payment processPayment(PaymentRequest request) {
////        Booking booking = bookingRepository.findById(request.getBookingId())
////                .orElseThrow(() -> new RuntimeException("Booking not found"));
//
//        Voucher voucher = null;
//
//        if (request.getVoucherCode() != null) {
//            voucher = voucherRepository.findByCode(request.getVoucherCode())
//                    .orElseThrow(() -> new RuntimeException("Invalid voucher"));
//
//        }
//
//        // Choose payment provider dynamically
//        if(request.getPaymentMethod() == PaymentMethod.PAYPAL) {}
//        PaymentGateway gateway = paymentGatewayFactory.getPaymentGateway(request.getPaymentGateway());
//        String transactionRef = gateway.charge(request);
//
//        Payment payment = new Payment();
//        payment.setBookingId(request.getBookingId());
//        payment.setTotalPrice(request.getTotalPrice());
//        payment.setVoucher(voucher);
//        payment.setTransactionReference(transactionRef);
//        payment.setPaymentStatus(PaymentStatus.PAID);
//        payment.setPaymentMethod(request.getPaymentMethod());
//        payment.setPaymentDate(Instant.now());
//
//        return paymentRepository.save(payment);
//    }

//    public Payment processRefund(RefundRequest refundRequest) {
//
//
//        Payment payment = paymentRepository.findById(refundRequest.getPaymentId())
//                .orElseThrow(() -> new RuntimeException("Payment not found"));
//
//        if (payment.getPaymentStatus() == PaymentStatus.REFUNDED || payment.getPaymentStatus() == PaymentStatus.PARTIALLY_REFUNDED) {
//            throw new RuntimeException("Already refunded");
//        }
//        if(payment.getPaymentMethod() == PaymentMethod.GATEWAY) {}
//        // Get the correct payment gateway
//        PaymentGateway gateway = paymentGatewayFactory.getPaymentGateway(payment.getPaymentGateway());
//        boolean refunded = gateway.refund(payment.getTransactionReference(), refundRequest.getRefundAmount());
//
//        if (!refunded) throw new RuntimeException("Refund failed");
//
//        payment.setRefundAmount(refundRequest.getRefundAmount());
//        payment.setRefundDate(Instant.now());
//        payment.setRefundReason(refundRequest.getRefundReason());
//        payment.setPaymentStatus(refundRequest.getRefundAmount().equals(payment.getTotalPrice()) ?
//                PaymentStatus.REFUNDED : PaymentStatus.PARTIALLY_REFUNDED);
//
//        return paymentRepository.save(payment);
//
//        // cần có policy tính toán tiền refund riêng dưới be dựa trên totalPrice và ko cần nhận vào từ request
//    }

}
