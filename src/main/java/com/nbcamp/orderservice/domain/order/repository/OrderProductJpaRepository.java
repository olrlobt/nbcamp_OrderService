package com.nbcamp.orderservice.domain.order.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbcamp.orderservice.domain.order.entity.OrderProduct;

public interface OrderProductJpaRepository extends JpaRepository<OrderProduct, UUID> {
}
