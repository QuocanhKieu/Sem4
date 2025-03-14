package com.devteria.payment.service;

import com.devteria.payment.configuration.SecurityUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.devteria.payment.entity.VoucherUsage;
import com.devteria.payment.repository.VoucherUsageRepository;
import org.springframework.stereotype.Service;

import com.devteria.payment.dto.VoucherDTO;
import com.devteria.payment.entity.Voucher;
import com.devteria.payment.repository.VoucherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;
    private final SecurityUtils securityUtils;

    /**
     * Creates a new voucher from the given data.
     *
     * @param voucherDTO - The data needed to create the voucher.
     * @return the created voucher.
     */
    public Voucher createVoucher(VoucherDTO voucherDTO) {
        // Validate input data
        if (voucherDTO.getValidFrom().isAfter(voucherDTO.getValidUntil())) {
            throw new IllegalArgumentException("Valid from date cannot be after the valid until date.");
        }

        // Check if the voucher code already exists
        if (voucherRepository.existsByCode(voucherDTO.getCode())) {
            throw new IllegalArgumentException("Voucher code already exists.");
        }

        // Create new Voucher entity from DTO
        Voucher voucher = Voucher.builder()
                .id(UUID.randomUUID().toString()) // Generate unique ID
                .code(voucherDTO.getCode())
                .description(voucherDTO.getDescription())
                .discountAmount(voucherDTO.getDiscountAmount())
                .isPercentage(voucherDTO.getIsPercentage())
                .maxUsagePerUser(voucherDTO.getMaxUsagePerUser())
                .totalUsageLimit(voucherDTO.getTotalUsageLimit())
                .validFrom(voucherDTO.getValidFrom())
                .validUntil(voucherDTO.getValidUntil())
                .active(true)
                .totalUsed(0) // Initially, no usage
                .build();

        // Save to the repository
        return voucherRepository.save(voucher);
    }


    /**
     * Fetch all valid vouchers for the authenticated user.
     */
    public List<Voucher> getValidVouchersForUser() {
        String userId = securityUtils.getCurrentUserId();
        return voucherRepository.findValidVouchersForUser(userId, Instant.now());
    }

    /**
     * Redeem a voucher for the authenticated user.
     */
    public boolean redeemVoucher(String voucherId) {
        String userId = securityUtils.getCurrentUserId();

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        VoucherUsage usage = voucherUsageRepository.findByUserIdAndVoucherId(userId, voucherId)
                .orElse(VoucherUsage.builder()
                        .userId(userId)
                        .voucher(voucher)
                        .usageCount(0)
                        .lastUsedAt(Instant.now())
                        .build()
                );

        // Validate usage conditions
        if (usage.getUsageCount() >= voucher.getMaxUsagePerUser()) {
            throw new RuntimeException("User has already reached the maximum usage limit for this voucher.");
        }
        if (voucher.getTotalUsed() >= voucher.getTotalUsageLimit()) {
            throw new RuntimeException("Voucher has reached its maximum global usage limit.");
        }
        if (voucher.getValidUntil().isBefore(Instant.now())) {
            throw new RuntimeException("Voucher has expired.");
        }

        // Update usage records
        usage.setUsageCount(usage.getUsageCount() + 1);
        usage.setLastUsedAt(Instant.now());
        voucher.setTotalUsed(voucher.getTotalUsed() + 1);

        voucherUsageRepository.save(usage);
        voucherRepository.save(voucher);

        return true;
    }
}
