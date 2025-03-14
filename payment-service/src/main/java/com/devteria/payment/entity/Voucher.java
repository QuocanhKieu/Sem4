package com.devteria.payment.entity; // package com.devteria.payment.entity;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(unique = true, nullable = false)
    String code;

    String description;

    @Column(nullable = false)
    BigDecimal discountAmount; // Money-safe type

    @JsonProperty("isPercentage")  // Explicitly set JSON property name
    @Column(nullable = false)
    boolean isPercentage; // true = percentage, false = fixed amount

    @Column(nullable = false)
    int maxUsagePerUser;

    @Column(nullable = false)
    int totalUsageLimit;

    @Column(nullable = false)
    int totalUsed = 0; // Tracks how many times the voucher has been used

    @Column(nullable = false)
    Instant validFrom;

    @Column(nullable = false)
    Instant validUntil;

    @Column(nullable = false)
    boolean active = true;

    // Getters and Setters
}

// CREATE TABLE vouchers (
//        id INT PRIMARY KEY AUTO_INCREMENT,
//        code VARCHAR(50) UNIQUE NOT NULL,
// description TEXT,
// discount_amount DECIMAL(10,2) NOT NULL, -- Store money safely
// is_percentage BOOLEAN NOT NULL,  -- True = percentage, False = fixed amount
// max_usage_per_user INT NOT NULL,
// total_usage_limit INT NOT NULL,
// total_used INT DEFAULT 0,
// valid_from DATETIME NOT NULL,
// valid_until DATETIME NOT NULL,
// active BOOLEAN DEFAULT TRUE
// );
