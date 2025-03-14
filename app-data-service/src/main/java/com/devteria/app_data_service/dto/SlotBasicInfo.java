package com.devteria.app_data_service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SlotBasicInfo {
    String slotId;
    String slotNumber;
    String slotBookedQr;
}
