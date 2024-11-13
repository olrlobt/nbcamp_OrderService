package com.nbcamp.orderservice.domain.review.entity;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
import com.nbcamp.orderservice.domain.order.entity.Order;
import com.nbcamp.orderservice.domain.review.dto.ReviewRequest;
import com.nbcamp.orderservice.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id = UUID.randomUUID();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "grade", nullable = false)
	private int grade;

	public static Review create(ReviewRequest request,User user, Order order){
		return Review.builder()
			.user(user)
			.order(order)
			.content(request.content())
			.grade(request.grade())
			.build();
	}
}
