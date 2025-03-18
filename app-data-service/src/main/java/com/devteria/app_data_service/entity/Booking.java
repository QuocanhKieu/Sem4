package com.devteria.app_data_service.entity;

import com.devteria.app_data_service.configuration.VietnamTimeDeserializer;
import com.devteria.app_data_service.configuration.VietnamTimeSerializer;
import com.devteria.app_data_service.dto.SlotBasicInfo;
import com.devteria.app_data_service.enums.BookingStatusEnums;
import com.devteria.app_data_service.enums.ServiceProvidedEnums;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "bookings")
@CompoundIndexes({
        @CompoundIndex(name = "booking_time_idx", def = "{'locationId': 1, 'startDateTime': 1, 'endDateTime': 1}")
})
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class    Booking {
    @Id
    @Field("_id")
    String id;

    List<SlotBasicInfo> slotBasicInfos;
    String userId;
    String locationId;

    ServiceProvidedEnums type;
    BookingStatusEnums status;// get updated when confirmed
    String canceledReason;// get updated when confirmed

    Integer wattHours;

    BigDecimal duration; // calculate

    BigDecimal price;//  calculate
    BigDecimal finalPrice;// get updated when confirmed

    String voucherId;// get updated when confirmed
    BigDecimal voucherAmount;// get updated when confirmed

//    @Indexed(expireAfterSeconds = 60) // Deletes only PENDING records
    @CreatedDate
    @JsonSerialize(using = VietnamTimeSerializer.class)

    Instant createdAt; // auto created


    @JsonSerialize(using = VietnamTimeSerializer.class)
    @JsonDeserialize(using = VietnamTimeDeserializer.class)  // Convert from GMT+7 → UTC (for backend storage)
    Instant startDateTime;
    @JsonSerialize(using = VietnamTimeSerializer.class)
    @JsonDeserialize(using = VietnamTimeDeserializer.class)  // Convert from GMT+7 → UTC (for backend storage)
    // update khi ko dùng thành instant.now để clear slots
    Instant endDateTime;

//    // lý thuyết
//    Instant bookedEndDateTime;
}
