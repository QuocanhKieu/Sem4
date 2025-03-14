package com.devteria.app_data_service.service;

import com.devteria.app_data_service.entity.Location;
import com.devteria.app_data_service.entity.ChargingSlot;
import com.devteria.app_data_service.repository.ChargingSlotRepository;
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
public class ChargingSlotService {
    private final ChargingSlotRepository chargingSlotRepository;

    // ✅ Create a new parking slot
    public ChargingSlot createChargingSlot(ChargingSlot chargingSlot) {
        return chargingSlotRepository.save(chargingSlot);
    }

    // ✅ Get all parking slots
    public List<ChargingSlot> getAllChargingSlot() {
        List<ChargingSlot> chargingSlot = chargingSlotRepository.findAll();
        return chargingSlot;
    }

    // ✅ Get a parking slot by ID
    public Optional<ChargingSlot> getChargingSlotById(String id) {
        return chargingSlotRepository.findById(id);
    }

    // ✅ Get all parking slots for a location
    public List<ChargingSlot> getChargingSlotByLocation(String locationId) {
        return chargingSlotRepository.findByLocationId(locationId);
    }

    // ✅ Update a parking slot
    public Optional<ChargingSlot> updateChargingSlot(String id, ChargingSlot updatedSlot) {
        return chargingSlotRepository.findById(id)
                .map(existingSlot -> {
                    existingSlot.setGate(updatedSlot.getGate());
                    existingSlot.setSlotNumber(updatedSlot.getSlotNumber());
                    existingSlot.setStatus(updatedSlot.getStatus());
                    return chargingSlotRepository.save(existingSlot);
                });
    }

    // ✅ Delete a parking slot
    public void deleteChargingSlot(String id) {
        chargingSlotRepository.deleteById(id);
    }

    public List<ChargingSlot> createChargingSlotsForLocation(Location location) {
        List<ChargingSlot> chargingSlots = new ArrayList<>(); // ✅ Naming convention fix
        List<String> gates = List.of("11W", "22W", "50W", "100W");


            for (String gate : gates) {
                for (int i = 1; i <= 14; i++) {
                    String slotNumber = "A" + i;

                    ChargingSlot slot = ChargingSlot.builder()
                            .id(UUID.randomUUID().toString())  // Generate unique ID
                            .locationId(location.getId())
                            .gate(gate)  // Assign gate
                            .slotNumber(slotNumber)
                            .status("AVAILABLE")
                            .build();

                    chargingSlots.add(slot);
                }
            }

        return chargingSlotRepository.saveAll(chargingSlots); // ✅ Corrected: repository name
    }
}
