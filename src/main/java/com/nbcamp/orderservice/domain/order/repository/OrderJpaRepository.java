package com.nbcamp.orderservice.domain.order.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbcamp.orderservice.domain.order.entity.Order;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
	boolean existsByIdAndUserId(UUID orderId, UUID userId);
}
