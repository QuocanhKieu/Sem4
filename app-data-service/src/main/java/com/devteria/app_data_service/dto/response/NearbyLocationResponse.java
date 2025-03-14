package com.devteria.app_data_service.dto.response;//package com.devteria.app_data_service.dto.response;

import com.devteria.app_data_service.enums.LocationServiceEnums;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NearbyLocationResponse {
    String id;
    String name;
    String address;
    List<String> images;
    List<LocationServiceEnums> services;
    Double latitude;
    Double longitude;
    Double distance;
}

