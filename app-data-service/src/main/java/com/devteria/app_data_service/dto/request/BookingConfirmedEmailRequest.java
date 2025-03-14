package com.devteria.app_data_service.dto.request;

import com.devteria.app_data_service.dto.SlotBasicInfo;
import com.devteria.app_data_service.entity.Booking;
import com.devteria.app_data_service.enums.ServiceProvidedEnums;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class BookingConfirmedEmailRequest {
    String locationName;
    String locationAddress;
    String userId;
    String bookingId;
    Instant startDateTime;
    Instant endDateTime;
    List<SlotBasicInfo> slotBasicInfos;
    ServiceProvidedEnums type;
    Integer wattHours;
    Double duration; // calculate
    Double price;//  calculate
    Double finalPrice;// get updated when confirmed
    Double voucherAmount;// get updated when confirmed

}
