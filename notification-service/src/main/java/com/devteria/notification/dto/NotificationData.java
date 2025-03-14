package com.devteria.notification.dto;


import com.devteria.notification.dto.request.BookingConfirmedEmailRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationData {
    String userId;
    String bookingId;
    BookingConfirmedEmailRequest bookingConfirmedEmailRequest;
    String title;
    String message;
}
