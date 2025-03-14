package com.devteria.identity.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.devteria.identity.configuration.AuthenticationRequestInterceptor;
import com.devteria.identity.dto.request.ApiResponse;
import com.devteria.identity.dto.request.EmailVerifyOTPRequest;

@FeignClient(
        name = "notification-service",
        url = "${app.services.notification}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface NotificationClient {
    @PostMapping(value = "/email/internal/send-email-verification-otp", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<String> sendEmailVerificationOTP(@RequestBody EmailVerifyOTPRequest request);
}
