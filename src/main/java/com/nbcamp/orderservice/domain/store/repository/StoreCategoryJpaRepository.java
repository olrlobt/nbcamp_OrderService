package com.nbcamp.orderservice.domain.store.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbcamp.orderservice.domain.store.entity.StoreCategory;

public interface StoreCategoryJpaRepository extends JpaRepository<StoreCategory, UUID> {



}
