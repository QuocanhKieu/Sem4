package com.devteria.notification.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingData {
    String userId;
    BookingData bookingData;
    String title;
    String message;
}
