package com.nbcamp.orderservice.domain.category.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbcamp.orderservice.domain.category.entity.Category;

public interface CategoryJpaRepository extends JpaRepository<Category, UUID> {
}
