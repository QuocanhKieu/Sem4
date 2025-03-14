package com.devteria.payment.repository;

import com.devteria.payment.entity.Voucher;
import com.devteria.payment.entity.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, String> {

    // Find usage record for a user and a specific voucher
    Optional<VoucherUsage> findByUserIdAndVoucherId(String userId, String voucherId);
}
