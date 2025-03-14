package com.devteria.app_data_service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import com.devteria.app_data_service.enums.LocationServiceEnums;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "locations") // ✅ MongoDB Collection Name
public class Location {
    @Id
    private String id;
    private String name;
    private String address;
    private List<String> images;

        @GeoSpatialIndexed // ✅ Enables geospatial indexing for location field
    private GeoJsonPoint location;

    private List<LocationServiceEnums> services;

    @Transient // ✅ Exclude from MongoDB storage
    private double distance;
}
