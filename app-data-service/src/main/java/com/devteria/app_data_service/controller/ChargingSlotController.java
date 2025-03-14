package com.devteria.app_data_service.controller;

import com.devteria.app_data_service.entity.Location;
import com.devteria.app_data_service.entity.ChargingSlot;
import com.devteria.app_data_service.service.ChargingSlotService;
import com.devteria.app_data_service.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/charging-slots")
@RequiredArgsConstructor
public class ChargingSlotController {
    private final ChargingSlotService chargingSlotService;
    private final LocationService locationService;
    // ✅ Create a new parking slot
//    @PostMapping
//    public ResponseEntity<ChargingSlot> createChargingSlot(@RequestBody ChargingSlot chargingSlot) {
//        ChargingSlot createdSlot = chargingSlotservice.createChargingSlot(chargingSlot);
//        return ResponseEntity.ok(createdSlot);
//    }
//
//    // ✅ Get all parking slots
//    @GetMapping
//    public ResponseEntity<List<ChargingSlot>> getAllChargingSlots() {
//        return ResponseEntity.ok(chargingSlotservice.getAllChargingSlots());
//    }
//
//    // ✅ Get a single parking slot by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<ChargingSlot> getChargingSlotById(@PathVariable String id) {
//        Optional<ChargingSlot> chargingSlot = chargingSlotservice.getChargingSlotById(id);
//        return chargingSlot.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
//    }
//
    // ✅ Get all parking slots for a location
    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<ChargingSlot>> getChargingSlotsByLocation(@PathVariable String locationId) {
        return ResponseEntity.ok(chargingSlotService.getChargingSlotByLocation(locationId));
    }
//
//    // ✅ Update a parking slot
//    @PutMapping("/{id}")
//    public ResponseEntity<ChargingSlot> updateChargingSlot(@PathVariable String id, @RequestBody ChargingSlot chargingSlot) {
//        return chargingSlotservice.updateChargingSlot(id, chargingSlot)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    // ✅ Delete a parking slot
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteChargingSlot(@PathVariable String id) {
//        chargingSlotservice.deleteChargingSlot(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    // ✅ Generate slots for all locations
//    @PostMapping("/generate")
//    public ResponseEntity<List<ChargingSlot>> generateChargingSlotsForAllLocations() {
//        List<Location> locations = locationService.getAllLocations();
//        List<ChargingSlot> allSlots = new ArrayList<>();
//
//        for (Location location : locations) {
//            List<ChargingSlot> slots = chargingSlotService.createChargingSlotsForLocation(location);
//            allSlots.addAll(slots);
//        }
//
//        return ResponseEntity.ok(allSlots);
//    }
}
