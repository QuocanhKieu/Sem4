package com.devteria.app_data_service.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ConfirmedBookingRequest {
    String bookingId;
    Double finalPrice;
    String voucherId;
    Double voucherAmount;
}
