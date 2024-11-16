package com.nbcamp.orderservice.domain.store.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbcamp.orderservice.domain.store.entity.Store;

public interface StoreJpaRepository extends JpaRepository<Store, UUID> {

	boolean existsByIdAndUserId(UUID storeId, UUID userId);
}
