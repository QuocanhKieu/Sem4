package com.devteria.app_data_service.dto.request;

import com.devteria.app_data_service.configuration.VietnamTimeDeserializer;
import com.devteria.app_data_service.configuration.VietnamTimeSerializer;
import com.devteria.app_data_service.dto.SlotBasicInfo;
import com.devteria.app_data_service.enums.ServiceProvidedEnums;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)

public class BookingRequest {
    String locationId;
    ServiceProvidedEnums type;
    Integer wattHours;
    @JsonSerialize(using = VietnamTimeSerializer.class)  // Convert from UTC → GMT+7 (for frontend)
    @JsonDeserialize(using = VietnamTimeDeserializer.class)  // Convert from GMT+7 → UTC (for backend storage)
    Instant startDateTime;
    @JsonSerialize(using = VietnamTimeSerializer.class)  // Convert from UTC → GMT+7 (for frontend)
    @JsonDeserialize(using = VietnamTimeDeserializer.class)  // Convert from GMT+7 → UTC (for backend storage)
    Instant endDateTime;
    List<SlotBasicInfo> SlotBasicInfos;
}
