package com.nbcamp.orderservice.domain.ai.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import com.nbcamp.orderservice.domain.product.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	name = "p_ai_request_log"
)
public class AIRequestLog {

	@Id
	@Column(name = "id", columnDefinition = "uuid comment 'AI 기록 고유 번호'")
	private UUID id = UUID.randomUUID();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false, columnDefinition = "uuid comment '상품 고유 번호'")
	private Product product;

	@Column(name = "request", nullable = false, columnDefinition = "varchar comment '요청문'")
	private String request;

	@Column(name = "response", nullable = false, columnDefinition = "varchar comment '답변'")
	private String response;

	@CreatedDate
	@Column(name = "created_at", updatable = false, columnDefinition = "timestamp comment '생성일'")
	private LocalDateTime createdAt;

	@CreatedBy
	@Column(name = "created_by", updatable = false, columnDefinition = "uuid comment '생성자'")
	private UUID createdBy;

}
