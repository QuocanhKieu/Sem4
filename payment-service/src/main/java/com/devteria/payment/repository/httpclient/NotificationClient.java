 package com.devteria.payment.repository.httpclient;

 import com.devteria.payment.configuration.AuthenticationRequestInterceptor;
 import com.devteria.payment.dto.ApiResponse;
 import org.springframework.cloud.openfeign.FeignClient;
 import org.springframework.http.MediaType;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;
 import com.devteria.payment.dto.request.BookingRefundedEmailRequest;


 @FeignClient(name = "notification-service", url = "${app.services.notification}",
        configuration = { AuthenticationRequestInterceptor.class })
 public interface NotificationClient {
    @PostMapping(value = "/internal/email/send-email-booking-refund", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<String> sendEmailBookingRefund(@RequestBody BookingRefundedEmailRequest request);

 }
