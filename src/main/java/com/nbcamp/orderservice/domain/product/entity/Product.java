package com.nbcamp.orderservice.domain.product.entity;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
import com.nbcamp.orderservice.domain.common.DisplayStatus;
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
	name = "product"
)
public class Product extends BaseTimeEntity {

	@Id
	@Column(name = "id", columnDefinition = "uuid comment '상품 고유 번호'")
	private UUID id = UUID.randomUUID();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false, columnDefinition = "uuid comment '매장 고유 번호'")
	private Store store;

	@Column(name = "name", nullable = false, columnDefinition = "varchar comment '상품명'")
	private String name;

	@Column(name = "description", nullable = false, columnDefinition = "varchar comment '상품설명'")
	private String description;

	@Column(name = "price", nullable = false, columnDefinition = "int comment '가격'")
	private int price;

	@Enumerated(EnumType.STRING)
	@Column(name = "display_status", nullable = false, columnDefinition = "varchar comment '노출상태'")
	private DisplayStatus displayStatus;

}
