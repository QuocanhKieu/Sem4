package com.devteria.app_data_service.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ConfirmedBookingRequest {
    String bookingId;
    BigDecimal finalPrice;
    String voucherId;
    BigDecimal voucherAmount;
}
