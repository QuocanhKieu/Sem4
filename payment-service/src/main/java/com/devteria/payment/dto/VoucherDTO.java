package com.devteria.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
    private String code;
    private String description;
    private BigDecimal discountAmount; // Money-safe type

    @JsonProperty("isPercentage") // Ensure JSON maps correctly to Java field
    private boolean isPercentage; // true = percentage, false = fixed amount

    private int maxUsagePerUser;
    private int totalUsageLimit;
    private Instant validFrom;
    private Instant validUntil;

    @JsonProperty("isPercentage") // Ensure correct serialization and deserialization
    public boolean getIsPercentage() {
        return isPercentage;
    }
}
