package com.devteria.app_data_service.service;

import com.devteria.app_data_service.dto.response.NearbyLocationResponse;
import com.devteria.app_data_service.enums.LocationServiceEnums;
import com.devteria.app_data_service.repository.impl.CustomLocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // ✅ Ensures read operations don't start unnecessary transactions
public class LocationService {

    private final CustomLocationRepository customLocationRepository;

    public LocationService(CustomLocationRepository customLocationRepository) {
        this.customLocationRepository = customLocationRepository;
    }

    public List<NearbyLocationResponse> getNearbyLocations(double longitude, double latitude, double maxDistance,
                                                      List<LocationServiceEnums> services, String searchText) {
        // ✅ Fetch nearby locations and map them to DTO
        return customLocationRepository.findNearbyWithDistance(longitude, latitude, maxDistance, services, searchText);
    }
}
