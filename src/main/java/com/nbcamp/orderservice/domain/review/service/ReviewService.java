package com.nbcamp.orderservice.domain.review.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.order.entity.Order;
import com.nbcamp.orderservice.domain.order.repository.OrderJpaRepository;
import com.nbcamp.orderservice.domain.review.dto.ReviewCursorResponse;
import com.nbcamp.orderservice.domain.review.dto.ReviewDetailsCursorResponse;
import com.nbcamp.orderservice.domain.review.dto.ReviewRequest;
import com.nbcamp.orderservice.domain.review.dto.ReviewResponse;
import com.nbcamp.orderservice.domain.review.entity.Review;
import com.nbcamp.orderservice.domain.review.repository.ReviewJpaRepository;
import com.nbcamp.orderservice.domain.review.repository.ReviewQueryRepository;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.store.repository.StoreJpaRepository;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.domain.user.service.UserService;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

	private final ReviewJpaRepository reviewJpaRepository;
	private final ReviewQueryRepository reviewQueryRepository;
	private final OrderJpaRepository orderJpaRepository;
	private final StoreJpaRepository storeJpaRepository;
	private final UserService userService;

	@Transactional
	public ReviewResponse createReview(User user, UUID orderId, ReviewRequest request) {
		Order order = findByOrderId(orderId);
		validateOrderAndReviewUser(order, user);
		validateOrderComplete(order);
		checkExistingReview(order, user);
		Review review = reviewJpaRepository.save(Review.create(request, user, order));
		order.getStore().addStoreGrade(review.getGrade());

		return new ReviewResponse(review);
	}

	@Transactional(readOnly = true)
	public Slice<ReviewCursorResponse> getCursorReview(
		UUID storeId,
		Pageable pageable,
		SortOption sortOption
	) {
		Store store = findByStore(storeId);
		return reviewQueryRepository.getAllReviewInStore(
			store,
			pageable,
			sortOption,
			false);
	}

	@Transactional(readOnly = true)
	public Slice<ReviewCursorResponse> getCursorReviewAdmin(
		UUID storeId,
		Pageable pageable,
		SortOption sortOption
	) {
		Store store = findByStore(storeId);
		return reviewQueryRepository.getAllReviewInStore(
			store,
			pageable,
			sortOption,
			true);
	}

	@Transactional(readOnly = true)
	public Slice<ReviewDetailsCursorResponse> getDetailsCursorUserReview(UUID userId, Pageable pageable){
		User user = userService.findById(userId);
		return reviewQueryRepository.getAllReviewInUser(user, pageable);
	}

	@Transactional
	public ReviewResponse updateReview(User user, UUID reviewId, ReviewRequest request) {
		Review review = findByIdCustom(reviewId);
		validateUserInReview(review.getId(), user.getId());
		review.update(request);

		return new ReviewResponse(review);
	}

	@Transactional
	public void deleteReview(User user, UUID reviewId) {
		Review review = findByIdCustom(reviewId);
		deleteByRole(user, review);
	}

	public void deleteByRole(User user, Review review) {
		if (user.getUserRole() == UserRole.CUSTOMER) {
			validateUserInReview(review.getId(), user.getId());
			deleteByCustomer(review, user);
		} else {
			deleteByAdmin(review, user);
		}
	}

	public void deleteByCustomer(Review review, User user) {
		review.delete(user.getId());
	}

	public void deleteByAdmin(Review review, User user) {
		review.delete(user.getId());
	}

	private void validateOrderComplete(Order order) {
		if (order.getOrderStatus() != OrderStatus.COMPLETED) {
			throw new IllegalArgumentException(ErrorCode.ORDER_INCOMPLETE_PROCESS.getMessage());
		}
	}

	private void validateOrderAndReviewUser(Order order, User user) {
		if (order.getUser().equals(user)) {
			throw new IllegalArgumentException(ErrorCode.NOT_MATCH_CONFIRM.getMessage());
		}
	}

	private void checkExistingReview(Order order, User user) {
		if (reviewJpaRepository.existsByUserIdAndOrderId(user.getId(), order.getId())) {
			throw new IllegalArgumentException(ErrorCode.EXISTING_REVIEW.getMessage());
		}
	}

	private void validateUserInReview(UUID reviewId, UUID userId) {
		if (Objects.equals(reviewId, userId)) {
			throw new IllegalArgumentException(ErrorCode.INVALID_REVIEW.getMessage());
		}
	}

	private Order findByOrderId(UUID orderId) {
		return orderJpaRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));
	}

	private Review findByIdCustom(UUID reviewId) {
		return reviewQueryRepository.findByIdCustom(reviewId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_REVIEW.getMessage()));
	}

	private Review findById(UUID reviewId) {
		return reviewJpaRepository.findById(reviewId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_REVIEW.getMessage()));
	}

	private Store findByStore(UUID storeId) {
		return storeJpaRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_STORE.getMessage()));
	}

}
