package com.devteria.app_data_service.controller;

import com.devteria.app_data_service.dto.response.NearbyLocationResponse;
import com.devteria.app_data_service.enums.LocationServiceEnums;
import com.devteria.app_data_service.service.LocationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/nearby")
    public List<NearbyLocationResponse> getNearbyLocations(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "5") double radiusInKm,
            @RequestParam(required = false) List<String> services, // Accepts services as a list of strings
            @RequestParam(required = false) String searchText) { // Optional filter

        // ✅ Convert service strings to Enum values safely
        List<LocationServiceEnums> serviceEnums = (services != null && !services.isEmpty()) ?
                services.stream()
                        .map(service -> {
                            try {
                                return LocationServiceEnums.valueOf(service.toUpperCase()); // Case insensitive
                            } catch (IllegalArgumentException e) {
                                return null; // Ignore invalid enums
                            }
                        })
                        .filter(enumVal -> enumVal != null) // Remove nulls
                        .collect(Collectors.toList()) :
                null;

        return locationService.getNearbyLocations(longitude, latitude, radiusInKm, serviceEnums, searchText);
    }
}
