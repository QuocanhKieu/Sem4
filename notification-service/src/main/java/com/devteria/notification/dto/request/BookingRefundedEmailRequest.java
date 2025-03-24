package com.devteria.notification.dto.request;

import com.devteria.notification.enums.ServiceProvidedEnums;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)

//public class BookingConfirmedEmailRequest {
//    String locationName;
//    String locationAddress;
//    private Map<String, String> slotQrCodes; // Key: slotNumber, Value: QR code
//    ServiceProvidedEnums type;
//    Integer wattHours;
//    Double duration; // calculate
//    Double price;//  calculate
//    Double finalPrice;// get updated when confirmed
//    Double voucherAmount;// get updated when confirmed
//
//}
public class BookingRefundedEmailRequest {
    String locationName;
    String locationAddress;
    String userId;
    String bookingId;
    Instant startDateTime;
    Instant endDateTime;
    private Map<String, String> slotQrCodes; // Key: slotNumber, Value: QR code
    ServiceProvidedEnums type;
    Integer wattHours;
    Double duration; // calculate
    Double price;//  calculate
    Double finalPrice;// get updated when confirmed
    Double voucherAmount;// get updated when confirmed

}