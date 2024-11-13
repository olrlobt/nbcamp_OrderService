package com.nbcamp.orderservice.domain.review.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.order.entity.Order;
import com.nbcamp.orderservice.domain.order.repository.OrderRepository;
import com.nbcamp.orderservice.domain.review.dto.ReviewCursorResponse;
import com.nbcamp.orderservice.domain.review.dto.ReviewRequest;
import com.nbcamp.orderservice.domain.review.dto.ReviewResponse;
import com.nbcamp.orderservice.domain.review.entity.Review;
import com.nbcamp.orderservice.domain.review.repository.ReviewJpaRepository;
import com.nbcamp.orderservice.domain.review.repository.ReviewQueryRepository;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.store.service.StoreService;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewJpaRepository reviewJpaRepository;
	private final ReviewQueryRepository reviewQueryRepository;
	private final OrderRepository orderRepository;
	private final StoreService storeService;

	@Transactional
	public ReviewResponse createReview(User user, String orderId, ReviewRequest request) {
		checkCustomerUserRole(user);
		Order order = findByOrderId(orderId);
		Review review = reviewJpaRepository.save(Review.create(request, user, order));
		order.getStore().updateStoreGrade(review.getGrade());

		return new ReviewResponse(
			review.getId(),
			review.getContent(),
			review.getGrade());
	}

	@Transactional(readOnly = true)
	public Slice<ReviewCursorResponse> findAllByStoreAndReview(String storeId, Pageable pageable) {
		Store store = storeService.findById(storeId);
		return reviewQueryRepository.getAllReviewInStore(pageable, store);

	}

	public void checkCustomerUserRole(User user) {
		if (user.getUserRole() == UserRole.OWNER ||
			user.getUserRole() == UserRole.MANAGER ||
			user.getUserRole() == UserRole.MASTER) {
			throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_PERMISSIONS.getMessage());
		}

	}

	private Order findByOrderId(String orderId) {
		return orderRepository.findById(UUID.fromString(orderId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));
	}
}
