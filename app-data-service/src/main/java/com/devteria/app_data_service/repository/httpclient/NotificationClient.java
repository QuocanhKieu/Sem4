 package com.devteria.app_data_service.repository.httpclient;

 import com.devteria.app_data_service.configuration.AuthenticationRequestInterceptor;
 import com.devteria.app_data_service.dto.ApiResponse;
 import com.devteria.app_data_service.dto.request.BookingConfirmedEmailRequest;
 import com.devteria.app_data_service.dto.request.ScheduledNotificationRequest;
 import org.springframework.cloud.openfeign.FeignClient;
 import org.springframework.http.MediaType;
 import org.springframework.scheduling.annotation.Scheduled;
 import org.springframework.web.bind.annotation.DeleteMapping;
 import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.PostMapping;
 import org.springframework.web.bind.annotation.RequestBody;

 import java.util.Map;

 @FeignClient(name = "notification-service", url = "${app.services.notification}",
        configuration = { AuthenticationRequestInterceptor.class })
 public interface NotificationClient {
    @PostMapping(value = "/email/internal/send-email-booking-confirmed", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<String> sendEmailBookingConfirmed(@RequestBody BookingConfirmedEmailRequest request);

    @PostMapping("/push-notification/internal/schedule")
    void scheduleNotification(@RequestBody ScheduledNotificationRequest scheduledNotificationRequest);

    @DeleteMapping("/internal/cancel/{bookingId}")
    void cancelNotification(@PathVariable Long bookingId);

 }
