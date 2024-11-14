package com.nbcamp.orderservice.domain.order.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.OrderType;
import com.nbcamp.orderservice.domain.order.dto.OrderRequest;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	name = "p_order"
)
public class Order extends BaseTimeEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_status", nullable = false)
	private OrderStatus orderStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_type", nullable = false)
	private OrderType orderType;

	@Column(name = "delivery_address")
	private String deliveryAddress;

	@Column(name = "request")
	private String request;

	@Column(name = "total_price", nullable = false)
	private int totalPrice;

	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<OrderProduct> orderProducts = new ArrayList<>();

	public static Order create(OrderRequest request, Store store, User user) {

		return Order.builder()
			.store(store)
			.user(user)
			.orderStatus(OrderStatus.PENDING)
			.orderType(request.type())
			.deliveryAddress(request.address())
			.request(request.request())
			.totalPrice(request.price())
			.build();

	}

	public void cancelOrder(UUID userId) {
		this.setDeletedAt(LocalDateTime.now());
		this.setDeletedBy(userId);

		for (OrderProduct orderProduct : orderProducts) {
			orderProduct.cancel(userId);
		}
	}

	public void updateOrderStatus(OrderStatus newStatus) {
		this.orderStatus = newStatus;
	}

	public void addOrderProduct(List<OrderProduct> orderProducts){
		this.orderProducts = orderProducts;
	}
}
