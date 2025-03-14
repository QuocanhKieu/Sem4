package com.devteria.app_data_service.mapper;

import com.devteria.app_data_service.dto.request.BookingRequest;
import com.devteria.app_data_service.entity.Booking;
import com.devteria.app_data_service.enums.BookingStatusEnums;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "id", ignore = true) // ID is auto-generated
    @Mapping(target = "userId", ignore = true) // Assume userId comes from another source
    @Mapping(target = "status", ignore = true) // Default status
    @Mapping(target = "slotBasicInfos", ignore = true) // Assume it's generated elsewhere
    @Mapping(target = "createdAt", ignore = true) // Created date is set by MongoDB
    Booking toBooking(BookingRequest request);
}