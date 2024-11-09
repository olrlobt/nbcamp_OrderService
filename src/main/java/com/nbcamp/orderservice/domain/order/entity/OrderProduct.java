package com.nbcamp.orderservice.domain.order.entity;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
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
	name = "p_order_product"
)
public class OrderProduct extends BaseTimeEntity {

	@Id
	@Column(name = "id")
	private UUID id = UUID.randomUUID();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	@Column(name = "total_price", nullable = false)
	private int totalPrice;
}

