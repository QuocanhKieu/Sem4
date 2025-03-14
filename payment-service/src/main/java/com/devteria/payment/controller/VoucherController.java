package com.devteria.payment.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.devteria.payment.dto.VoucherDTO;
import com.devteria.payment.entity.Voucher;
import com.devteria.payment.service.VoucherService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    /**
     * Endpoint to create a new voucher.
     *
     * @param voucherDTO - Data needed to create a voucher.
     * @return the created voucher.
     */
    @PostMapping
    public ResponseEntity<Voucher> createVoucher(@Valid @RequestBody VoucherDTO voucherDTO) {
        try {
            Voucher createdVoucher = voucherService.createVoucher(voucherDTO);
            return new ResponseEntity<>(createdVoucher, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("valid")
    public ResponseEntity<List<Voucher>> getValidVouchers() {
        return ResponseEntity.ok(voucherService.getValidVouchersForUser());
    }

    @PostMapping("/redeem/{voucherId}")
    public ResponseEntity<String> redeemVoucher(@PathVariable String voucherId) {
        voucherService.redeemVoucher(voucherId);
        return ResponseEntity.ok("Voucher redeemed successfully!");
    }
}
