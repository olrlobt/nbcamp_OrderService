package com.nbcamp.orderservice.domain.payment.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbcamp.orderservice.domain.payment.entity.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {
}
