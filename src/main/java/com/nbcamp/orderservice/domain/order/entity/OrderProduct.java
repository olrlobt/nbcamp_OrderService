package com.nbcamp.orderservice.domain.order.entity;

import java.util.UUID;
import java.util.stream.Collectors;

import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
import com.nbcamp.orderservice.domain.order.dto.OrderRequest;
import com.nbcamp.orderservice.domain.product.entity.Product;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

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

	// public static List<OrderProduct> create(Order order, List<Product> products,
	// 	List<OrderRequest.OrderProduct> productRequests) {
	// 	return productRequests.stream()
	// 		.map(productRequest -> {
	// 			Product product = products.stream()
	// 				.filter(p -> p.getId().equals(productRequest.productId()))
	// 				.findFirst()
	// 				.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_PRODUCT.getMessage()));
	//
	// 			return OrderProduct.builder()
	// 				.order(order)
	// 				.product(product)
	// 				.quantity(productRequest.quantity())
	// 				.totalPrice(productRequest.price())
	// 				.build();
	// 		})
	// 		.collect(Collectors.toList());
	// }

	public static OrderProduct create(Order order, Product product, int quantity){
		return OrderProduct.builder()
			.order(order)
			.product(product)
			.quantity(quantity)
			.totalPrice(quantity * product.getPrice())
			.build();
	}

	public void cancel(UUID userId) {
		this.setDeletedAt(LocalDateTime.now());
		this.setDeletedBy(userId);
	}
}

