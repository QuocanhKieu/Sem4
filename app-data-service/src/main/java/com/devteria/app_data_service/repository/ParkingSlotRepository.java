package com.devteria.app_data_service.repository;

import com.devteria.app_data_service.entity.ParkingSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSlotRepository extends MongoRepository<ParkingSlot, String> {
    List<ParkingSlot> findByLocationId(String locationId); // Get all slots for a location
}
