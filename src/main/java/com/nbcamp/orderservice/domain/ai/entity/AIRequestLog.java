package com.nbcamp.orderservice.domain.ai.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
	name = "ai_request_log"
)
public class AIRequestLog {

	@Id
	private UUID id = UUID.randomUUID();

	@Column(name = "request", nullable = false, columnDefinition = "varchar comment '요청문'")
	private String request;

	@Column(name = "response", nullable = false, columnDefinition = "varchar comment '답변'")
	private String response;

	@CreatedDate
	@Column(name = "created_at", updatable = false, columnDefinition = "timestamp comment '생성일'")
	private LocalDateTime createdAt;

	@CreatedBy
	private UUID createdBy;

}
