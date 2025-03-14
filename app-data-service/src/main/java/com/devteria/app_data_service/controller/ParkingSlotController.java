package com.devteria.app_data_service.controller;

import com.devteria.app_data_service.entity.Location;
import com.devteria.app_data_service.entity.ParkingSlot;
import com.devteria.app_data_service.service.LocationService;
import com.devteria.app_data_service.service.ParkingSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/parking-slots")
@RequiredArgsConstructor
public class ParkingSlotController {
    private final ParkingSlotService parkingSlotService;
    private final LocationService locationService;
    // ✅ Create a new parking slot
    @PostMapping
    public ResponseEntity<ParkingSlot> createParkingSlot(@RequestBody ParkingSlot parkingSlot) {
        ParkingSlot createdSlot = parkingSlotService.createParkingSlot(parkingSlot);
        return ResponseEntity.ok(createdSlot);
    }

    // ✅ Get all parking slots
    @GetMapping
    public ResponseEntity<List<ParkingSlot>> getAllParkingSlots() {
        return ResponseEntity.ok(parkingSlotService.getAllParkingSlots());
    }

    // ✅ Get a single parking slot by ID
    @GetMapping("/{id}")
    public ResponseEntity<ParkingSlot> getParkingSlotById(@PathVariable String id) {
        Optional<ParkingSlot> parkingSlot = parkingSlotService.getParkingSlotById(id);
        return parkingSlot.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get all parking slots for a location
    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<ParkingSlot>> getParkingSlotsByLocation(@PathVariable String locationId) {
        return ResponseEntity.ok(parkingSlotService.getParkingSlotsByLocation(locationId));
    }

    // ✅ Update a parking slot
    @PutMapping("/{id}")
    public ResponseEntity<ParkingSlot> updateParkingSlot(@PathVariable String id, @RequestBody ParkingSlot parkingSlot) {
        return parkingSlotService.updateParkingSlot(id, parkingSlot)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Delete a parking slot
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingSlot(@PathVariable String id) {
        parkingSlotService.deleteParkingSlot(id);
        return ResponseEntity.noContent().build();
    }

//    // ✅ Generate slots for all locations
//    @PostMapping("/generate")
//    public ResponseEntity<List<ParkingSlot>> generateParkingSlotsForAllLocations() {
//        List<Location> locations = locationService.getAllLocations();
//        List<ParkingSlot> allSlots = new ArrayList<>();
//
//        for (Location location : locations) {
//            List<ParkingSlot> slots = parkingSlotService.createParkingSlotsForLocation(location);
//            allSlots.addAll(slots);
//        }
//
//        return ResponseEntity.ok(allSlots);
//    }
}
