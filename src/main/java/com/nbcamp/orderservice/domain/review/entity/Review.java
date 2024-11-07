package com.nbcamp.orderservice.domain.review.entity;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
import com.nbcamp.orderservice.domain.order.entity.Order;
import com.nbcamp.orderservice.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
	name = "p_review"
)
public class Review extends BaseTimeEntity {

	@Id
	@Column(name = "id", columnDefinition = "uuid comment '리뷰 고유 번호'")
	private UUID id = UUID.randomUUID();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, columnDefinition = "uuid comment '회원 고유 번호'")
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false, columnDefinition = "uuid comment '주문 고유 번호'")
	private Order order;

	@Column(name = "content", nullable = false, columnDefinition = "varchar comment '리뷰 내용'")
	private String content;

	@Column(name = "grade", nullable = false, columnDefinition = "int comment '평점'")
	private int grade;
}
