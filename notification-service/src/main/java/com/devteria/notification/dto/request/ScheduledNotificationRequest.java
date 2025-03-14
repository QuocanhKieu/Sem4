package com.devteria.notification.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScheduledNotificationRequest {
    String bookingId;
    Instant startDateTime;
}