package com.devteria.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devteria.payment.entity.Voucher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, String> {
    boolean existsByCode(String code); // Check if voucher code already exists

    Optional<Voucher> findByCode(String code);

    // Fetch valid vouchers available for a specific user
    @Query("""
           SELECT v FROM Voucher v
           WHERE v.validFrom <= :now 
           AND v.validUntil >= :now 
           AND v.totalUsed < v.totalUsageLimit
           AND NOT EXISTS (
               SELECT 1 FROM VoucherUsage vu 
               WHERE vu.userId = :userId 
               AND vu.voucher.id = v.id 
               AND vu.usageCount >= v.maxUsagePerUser
           )
           """)
    List<Voucher> findValidVouchersForUser(@Param("userId") String userId, @Param("now") Instant now);
}
