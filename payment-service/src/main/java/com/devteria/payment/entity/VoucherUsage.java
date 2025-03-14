package com.devteria.payment.entity; // package com.devteria.payment.entity;

import java.time.Instant;
import java.time.LocalDateTime;

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
@Table(name = "voucher_usages", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "voucher_id"}))
public class VoucherUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @Column(nullable = false)
    private int usageCount; // How many times this user used it

    @Column(nullable = false)
    private Instant lastUsedAt = Instant.now();

    // Getters and Setters
}
// CREATE TABLE voucher_usages (
//        id INT PRIMARY KEY AUTO_INCREMENT,
//        user_id INT NOT NULL,  -- User who used the voucher
//                voucher_id INT NOT NULL,  -- Reference to voucher
//                usage_count INT NOT NULL DEFAULT 1,  -- How many times this user used it
//                last_used_at DATETIME NOT NULL DEFAULT NOW(), -- Last time used
// CONSTRAINT fk_voucher FOREIGN KEY (voucher_id) REFERENCES vouchers(id) ON DELETE CASCADE,
// UNIQUE (user_id, voucher_id)  -- Prevent duplicate entries, we only update this row
// );
