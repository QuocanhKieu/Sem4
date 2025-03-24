 package com.devteria.app_data_service.repository.httpclient;

 import com.devteria.app_data_service.configuration.AuthenticationRequestInterceptor;
 import com.devteria.app_data_service.dto.ApiResponse;
 import com.devteria.app_data_service.dto.request.BookingConfirmedEmailRequest;
 import com.devteria.app_data_service.dto.request.RefundRequest;
 import com.devteria.app_data_service.dto.request.ScheduledNotificationRequest;
 import com.devteria.app_data_service.dto.request.PaymentRequest;
 import org.springframework.cloud.openfeign.FeignClient;
 import org.springframework.http.MediaType;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.DeleteMapping;
 import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;

 @FeignClient(name = "payment-service", url = "${app.services.payment}",
        configuration = { AuthenticationRequestInterceptor.class })
 public interface PaymentClient {
//    @PostMapping(value = "/email/internal/send-email-booking-confirmed", produces = MediaType.APPLICATION_JSON_VALUE)
//    ApiResponse<String> sendEmailBookingConfirmed(@RequestBody BookingConfirmedEmailRequest request);
//
//    @PostMapping("/push-notification/internal/schedule")
//    void scheduleNotification(@RequestBody ScheduledNotificationRequest scheduledNotificationRequest);

    @PostMapping("/payments/internal/refund")
    void refundOrder(@RequestBody RefundRequest refundRequest);

 }
// @PostMapping("internal/refund")
// public ResponseEntity<?> refundOrder(@RequestBody RefundRequest refundRequest) {
//     try {
//         String message = paymentService.refundPayment(refundRequest);
//         return ResponseEntity.ok(message);
//     } catch (RuntimeException e) {
//         return ResponseEntity.badRequest().body(e.getMessage());
//     }
// }