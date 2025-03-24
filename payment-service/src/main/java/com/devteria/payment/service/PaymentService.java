package com.devteria.payment.service;

import com.devteria.payment.constant.PaymentMethod;
import com.devteria.payment.constant.PaymentStatus;
import com.devteria.payment.dto.request.*;
import com.devteria.payment.entity.Payment;
import com.devteria.payment.entity.Voucher;
import com.devteria.payment.exception.AppException;
import com.devteria.payment.exception.ErrorCode;
import com.devteria.payment.payment.PaymentGateway;
import com.devteria.payment.payment.PaymentGatewayFactory;
import com.devteria.payment.repository.PaymentRepository;
import com.devteria.payment.repository.VoucherRepository;
import com.devteria.payment.repository.httpclient.BookingAppDataClient;
import com.devteria.payment.repository.httpclient.NotificationClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PayPalService payPalService;
    //    private final BookingRepository bookingRepository;
    private final VoucherRepository voucherRepository;
    private final PaymentGatewayFactory paymentGatewayFactory;
    private final BookingAppDataClient bookingAppDataClient;
    private final NotificationClient notificationClient;
    @Value("${paypal.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

//    public String processRefund(RefundRequest refundRequest){
//        Payment payment = paymentRepository.findByBookingId(refundRequest.getBookingId())
//                .orElseThrow(() -> new RuntimeException("Transaction not found"));
//
//        // 🛑 Check if already refunded
//        if (!payment.getPaymentStatus().equals(PaymentStatus.PAID)) {
//            throw new RuntimeException("Payment already refunded");
//        }
//
//        // 💰 Call PayPal refund API
//        String refundTransactionId = payPalService.refundPayment(transactionId, refundAmount, payment.getCurrency());
//
//        // ✅ Update payment record
//        payment.setPaymentStatus(
//                refundAmount.compareTo(payment.getTotalPrice()) == 0 ? PaymentStatus.REFUNDED : PaymentStatus.PARTIALLY_REFUNDED
//        );
//        payment.setRefundDate(Instant.now());
//        payment.setTransactionReference(refundTransactionId);
//        paymentRepository.save(payment);
//
//        return "Refund processed successfully. Refund ID: " + refundTransactionId;
//    }
public String refundPayment(RefundRequest refundRequest) {
    // Step 1: Get PayPal Access Token
    String accessToken = payPalService.getAccessToken();

    // Step 2: Retrieve Payment Record
    Payment payment = paymentRepository.findByBookingId(refundRequest.getBookingId())
            .orElseThrow(() -> new RuntimeException("Payment not found"));

    // Step 3: Validate Payment Status (Check if it's already refunded)
    if (!payment.getPaymentStatus().equals(PaymentStatus.PAID)) {
        throw new RuntimeException("Payment has already been refunded");
    }

    // Step 4: Construct Refund URL Dynamically
    String refundUrl = (payment.getRefundUrl() != null && !payment.getRefundUrl().isEmpty())
            ? payment.getRefundUrl()
            : baseUrl + "/v1/payments/sale/" + Optional.ofNullable(payment.getSaleId())
            .orElseThrow(() -> new RuntimeException("Sale ID is missing for payment")) + "/refund";

    // Step 5: Prepare HTTP Headers
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + accessToken);
    headers.setContentType(MediaType.APPLICATION_JSON);

    // Step 6: Prepare Request Body (Optional if full refund)
    Map<String, Object> body = new HashMap<>();
    body.put("amount", Map.of(
            "total", payment.getFinalPrice().subtract(payment.getTransactionFee()).toString(),
            "currency", payment.getCurrency()
    ));

    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    try {
        // Step 7: Send Refund Request
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                refundUrl, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Step 8: Extract Refund Transaction ID Safely
        String refundTransactionId = Optional.ofNullable(response.getBody())
                .map(responseBody -> responseBody.get("id"))
                .map(String::valueOf) // Safer conversion
                .orElseThrow(() -> new RuntimeException("Refund transaction ID missing in response"));

        // Step 9: Update Payment Record
        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.setRefundDate(Instant.now());
        payment.setRefundPaymentId(refundTransactionId);
        paymentRepository.save(payment);


        /// send email notify user about the refund
        notificationClient.sendEmailBookingRefund(BookingRefundedEmailRequest.builder().bookingId(refundRequest.getBookingId()).build());

        return refundTransactionId; // ✅ Return the refund ID
    } catch (HttpClientErrorException e) {
        throw new RuntimeException("PayPal API error: " + e.getResponseBodyAsString(), e);
    } catch (Exception e) {
        throw new RuntimeException("Unexpected error during refund process", e);
    }
}



    public String verifyPayment(PaymentRequest paymentRequest, String userId) {

        Optional<Payment> existingPayment = paymentRepository.findByBookingId(paymentRequest.getBookingId());
        if (existingPayment.isPresent()) {
            throw new AppException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }
        String accessToken = payPalService.getAccessToken();

//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + accessToken);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> request = new HttpEntity<>(headers);
//        ResponseEntity<String> response = restTemplate.exchange(
//                baseUrl + "/v2/checkout/orders/" + orderId,
//                HttpMethod.GET,
//                request,
//                String.class
//        );
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            JSONObject jsonResponse = new JSONObject(response.getBody());
//
//            String status = jsonResponse.getString("status");
//            String transactionId = jsonResponse.getString("id");
//            String currency = jsonResponse.getJSONArray("purchase_units")
//                    .getJSONObject(0).getJSONObject("amount").getString("currency_code");
//            BigDecimal amount = new BigDecimal(jsonResponse.getJSONArray("purchase_units")
//                    .getJSONObject(0).getJSONObject("amount").getString("value"));

        Voucher voucher = getVoucherOrNull(paymentRequest.getVoucherId());
        BigDecimal voucherAmount = calculateVoucherAmount(voucher, paymentRequest.getInitialPrice());

                // 🟢 Store the payment record
            Payment payment = Payment.builder()
            .bookingId(paymentRequest.getBookingId())
            .userId(userId)
            .initialPrice(paymentRequest.getInitialPrice())
            .finalPrice(paymentRequest.getPayPalPaymentDto().getTotalAmount())
            .paymentMethod(PaymentMethod.PAYPAL)
            .currency(paymentRequest.getPayPalPaymentDto().getCurrency())
            .paymentDate(paymentRequest.getPayPalPaymentDto().getTransactionTime())
            .paymentStatus(PaymentStatus.PAID)
            .paymentId(paymentRequest.getPayPalPaymentDto().getPaymentId())
            .saleId(paymentRequest.getPayPalPaymentDto().getSaleId())
            .payerId(paymentRequest.getPayPalPaymentDto().getPayerId())
            .buyerEmail(paymentRequest.getPayPalPaymentDto().getBuyerEmail())
            .merchantId(paymentRequest.getPayPalPaymentDto().getMerchantId())
            .transactionFee(paymentRequest.getPayPalPaymentDto().getTransactionFee())
            .refundUrl(paymentRequest.getPayPalPaymentDto().getRefundUrl())
            .voucher(voucher) // Assuming the request contains a Voucher object
            .voucherAmount(voucherAmount) // If available
            .build();


            paymentRepository.save(payment);


            try {
                bookingAppDataClient.confirmBooking(ConfirmedBookingRequest.builder()
                        .bookingId(payment.getBookingId())
                        .finalPrice(payment.getFinalPrice())
//                        .voucherId(payment.getVoucher().getId())
                        .voucherId(payment.getVoucher() != null ? payment.getVoucher().getId() : null) // Handle null voucher
                        .voucherAmount(voucherAmount)

                        .build());
                return "Payment Verified and Stored Successfully!";


            } catch (FeignException e) { // Handle Feign client errors
                throw new AppException(ErrorCode.CANNOT_CONFIRM_BOOKING);
            }
    }

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




    private Voucher getVoucherOrNull(String voucherId) {
        return voucherId == null ? null : voucherRepository.findById(voucherId).orElse(null);
    }

    private BigDecimal calculateVoucherAmount(Voucher voucher, BigDecimal initialPrice) {
        if (voucher == null) {
            return BigDecimal.ZERO; // No voucher applied
        }

        return (voucher.isPercentage()
                ? initialPrice.multiply(voucher.getDiscountAmount().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                : voucher.getDiscountAmount()
        ).setScale(2, RoundingMode.HALF_UP);
    }


}
