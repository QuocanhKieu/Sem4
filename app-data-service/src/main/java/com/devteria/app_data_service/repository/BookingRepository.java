package com.devteria.app_data_service.repository;

import com.devteria.app_data_service.entity.Booking;
import com.devteria.app_data_service.enums.BookingStatusEnums;
import com.devteria.app_data_service.enums.ServiceProvidedEnums;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends MongoRepository<Booking, String> {

//    Optional<Booking> findById(String id);
    List<Booking> findByStatusAndCreatedAtBefore(BookingStatusEnums status, Instant createdAt);


    @Query("""
        {
            "locationId": ?0,
            "type": ?3,
            "$or": [
                { 
                    "$and": [
                        { "startDateTime": { "$lt": ?2 } },
                        { "endDateTime": { "$gt": ?1 } }
                    ]
                }
            ]
        }
    """)
    List<Booking> findOverlappingBookings(String locationId, Instant startDateTime, Instant endDateTime, String type);

    List<Booking> findByUserId(String userId);


    // Find by userId and type (Charging or Parking)
    List<Booking> findByUserIdAndType(String userId, ServiceProvidedEnums type);

//    void updateStatusForExpiredBookings(BookingStatusEnums currentStatus, Instant expirationTime, BookingStatusEnums newStatus);
}

