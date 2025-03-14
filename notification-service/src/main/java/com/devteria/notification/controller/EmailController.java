package com.devteria.notification.controller;

import com.devteria.notification.configuration.SecurityUtils;
import com.devteria.notification.dto.ApiResponse;
import com.devteria.notification.dto.request.BookingConfirmedEmailRequest;
import com.devteria.notification.dto.request.EmailVerifyOTPRequest;
import com.devteria.notification.dto.request.SendEmailRequest;
import com.devteria.notification.dto.response.EmailResponse;
import com.devteria.notification.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailService emailService;
    SecurityUtils securityUtils;

    @PostMapping("/send")
    ApiResponse<String> sendEmail(@RequestBody SendEmailRequest request) {
        emailService.sendEmail(request); // Executes the method, but doesn't return anything
        return ApiResponse.<String>builder()
                .result("Email sent successfully") // Provide a meaningful success message
                .build();
    }


    @PostMapping("/internal/send-email-verification-otp")
    public ApiResponse<String> sendOtp(@RequestBody EmailVerifyOTPRequest request) {
        // Send OTP email
        emailService.sendOtpEmail(request.getTo().getEmail(), request.getTo().getName(), request.getOtp());

        return ApiResponse.<String>builder()
                .result("Email sent successfully") // Provide a meaningful success message
                .build();
    }

    @PostMapping("/internal/send-email-booking-confirmed")
    public ApiResponse<String> sendBookingConfirmed(@RequestBody BookingConfirmedEmailRequest request) {
        // Send OTP email
        emailService.sendBookingConfirmedEmail(securityUtils.getCurrentUserEmail(), request);

        return ApiResponse.<String>builder()
                .result("Booking Confirmed Email sent successfully") // Provide a meaningful success message
                .build();
    }
//    @KafkaListener(topics = "onboard-successful")
//    public void listen(String message){
//        log.info("Message received: {}", message);
//    }
}
