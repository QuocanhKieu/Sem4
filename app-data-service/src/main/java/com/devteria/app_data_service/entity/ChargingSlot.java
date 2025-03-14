package com.devteria.app_data_service.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "chargingSlots")
public class ChargingSlot {
    @Id
    private String id;

    private String locationId;
    private String gate;  // Added gate
    private String slotNumber;
    private String type;  // Should be "CHARGING"
    private String status;
}
