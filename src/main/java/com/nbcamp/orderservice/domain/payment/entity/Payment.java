package com.nbcamp.orderservice.domain.payment.entity;

import java.util.UUID;

import org.hibernate.annotations.Comment;

import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
import com.nbcamp.orderservice.domain.common.PaymentMethod;
import com.nbcamp.orderservice.domain.common.PaymentStatus;
import com.nbcamp.orderservice.domain.order.entity.Order;
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
	name = "p_payment"
)
public class Payment extends BaseTimeEntity {

	@Id
	@Column(name = "id")
	@Comment("uuid comment 결제 고유 번호")
	private UUID id = UUID.randomUUID();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@Comment("uuid comment 주문 고유 번호")
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@Comment("uuid comment 회원 고유 번호")
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false)
	@Comment("varchar comment 결제 상태")
	private PaymentStatus paymentStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false)
	@Comment("varchar comment 결제 수단")
	private PaymentMethod paymentMethod;

	@Column(name = "amount", nullable = false)
	@Comment("int comment 결제 금액")
	private int amount;

}
