package com.nbcamp.orderservice.domain.ai.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nbcamp.orderservice.domain.ai.entity.AIRequestLog;

public interface AIJpaRepository extends JpaRepository<AIRequestLog, UUID> {
}
