package com.devteria.app_data_service.repository;

import com.devteria.app_data_service.entity.ChargingSlot;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingSlotRepository extends MongoRepository<ChargingSlot, String> {
    List<ChargingSlot> findByLocationId(String locationId); // Get all slots for a location
}
