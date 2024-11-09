package com.nbcamp.orderservice.domain.product.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbcamp.orderservice.domain.product.entity.Product;

public interface ProductJpaRepository extends JpaRepository<Product, UUID> {
}
