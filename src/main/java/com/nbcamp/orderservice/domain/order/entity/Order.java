package com.nbcamp.orderservice.domain.order.entity;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.OrderType;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.user.entity.User;

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
	name = "p_order"
)
public class Order extends BaseTimeEntity {

	@Id
	@Column(name = "id", columnDefinition = "uuid comment '주문 고유 번호'")
	private UUID id = UUID.randomUUID();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false, columnDefinition = "uuid comment '매장 고유 번호'")
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, columnDefinition = "uuid comment '회원 고유 번호'")
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_status", nullable = false, columnDefinition = "varchar comment '주문 상태'")
	private OrderStatus orderStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_type", nullable = false, columnDefinition = "varchar comment '주문 타입'")
	private OrderType orderType;

	@Column(name = "delivery_address", columnDefinition = "varchar comment '배달 주소'")
	private String deliveryAddress;

	@Column(name = "request", columnDefinition = "varchar comment '요청 사항'")
	private String request;

	@Column(name = "total_price", nullable = false, columnDefinition = "int comment '주문 총 금액'")
	private int totalPrice;

}
