package com.devteria.payment.repository;

import com.devteria.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByTransactionReference(String transactionReference);

    Optional<Payment> findByBookingId(String bookingId);
}
