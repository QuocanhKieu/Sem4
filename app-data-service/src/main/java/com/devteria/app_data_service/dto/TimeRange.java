package com.devteria.app_data_service.dto;

import lombok.*;

import java.time.Instant;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeRange {
    private Instant start;
    private Instant end;

    public boolean isValid() {
        return start != null && end != null && start.isBefore(end);
    }

    public boolean overlapsWith(TimeRange other) {
        return this.start.isBefore(other.end) && this.end.isAfter(other.start);
    }
}
