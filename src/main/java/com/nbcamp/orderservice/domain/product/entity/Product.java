package com.nbcamp.orderservice.domain.product.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
import com.nbcamp.orderservice.domain.common.DisplayStatus;
import com.nbcamp.orderservice.domain.product.dto.ProductRequest;
import com.nbcamp.orderservice.domain.store.entity.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	name = "p_product"
)
public class Product extends BaseTimeEntity {

	@Id
	@Column(name = "id")
	private UUID id = UUID.randomUUID();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "price", nullable = false)
	private int price;

	@Enumerated(EnumType.STRING)
	@Column(name = "display_status", nullable = false)
	private DisplayStatus displayStatus;

	public static Product create(ProductRequest request, Store store) {
		return Product.builder()
			.store(store)
			.name(request.name())
			.description(request.description())
			.price(request.price())
			.displayStatus(request.status())
			.build();
	}

	public void update(ProductRequest request) {
		this.name = request.name();
		this.description = request.description();
		this.price = request.price();
		this.displayStatus = request.status();
	}

	public void delete() {
		this.setDeletedAt(LocalDateTime.now());
	}
}
