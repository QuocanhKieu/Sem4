package com.devteria.app_data_service.service;

import com.devteria.app_data_service.entity.Location;
import com.devteria.app_data_service.entity.ParkingSlot;
import com.devteria.app_data_service.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingSlotService {
    private final ParkingSlotRepository parkingSlotRepository;

    // ✅ Create a new parking slot
    public ParkingSlot createParkingSlot(ParkingSlot parkingSlot) {
        return parkingSlotRepository.save(parkingSlot);
    }

    // ✅ Get all parking slots
    public List<ParkingSlot> getAllParkingSlots() {
        List<ParkingSlot> parkingSlots = parkingSlotRepository.findAll();
        return parkingSlots;
    }

    // ✅ Get a parking slot by ID
    public Optional<ParkingSlot> getParkingSlotById(String id) {
        return parkingSlotRepository.findById(id);
    }

    // ✅ Get all parking slots for a location
    public List<ParkingSlot> getParkingSlotsByLocation(String locationId) {
        return parkingSlotRepository.findByLocationId(locationId);
    }

    // ✅ Update a parking slot
    public Optional<ParkingSlot> updateParkingSlot(String id, ParkingSlot updatedSlot) {
        return parkingSlotRepository.findById(id)
                .map(existingSlot -> {
                    existingSlot.setZone(updatedSlot.getZone());
                    existingSlot.setSlotNumber(updatedSlot.getSlotNumber());
                    existingSlot.setType(updatedSlot.getType());
                    existingSlot.setStatus(updatedSlot.getStatus());
                    return parkingSlotRepository.save(existingSlot);
                });
    }

    // ✅ Delete a parking slot
    public void deleteParkingSlot(String id) {
        parkingSlotRepository.deleteById(id);
    }

    public List<ParkingSlot> createParkingSlotsForLocation(Location location) {
        List<ParkingSlot> parkingSlots = new ArrayList<>(); // ✅ Naming convention fix
        String[] zones = {"A", "B", "C", "D", "E", "F"};

        for (String zone : zones) {
            for (int i = 1; i <= 6; i++) {
                ParkingSlot parkingSlot = ParkingSlot.builder()
                        .id(UUID.randomUUID().toString()) // Unique ID
                        .locationId(location.getId()) // Reference to location
                        .zone(zone) // A-F
                        .slotNumber(zone + i) // ✅ Fix: Correct field name
                        .type("PARKING")
                        .status("AVAILABLE")
                        .build();
                parkingSlots.add(parkingSlot);
            }
        }

        return parkingSlotRepository.saveAll(parkingSlots); // ✅ Corrected: repository name
    }
}
