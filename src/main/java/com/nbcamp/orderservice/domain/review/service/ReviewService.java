package com.nbcamp.orderservice.domain.review.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.common.OrderStatus;
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
import com.nbcamp.orderservice.domain.store.service.StoreService;
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
	private final StoreService storeService;
	private final UserService userService;

	@Transactional
	public ReviewResponse createReview(User user, String orderId, ReviewRequest request) {
		checkCustomerUserRole(user);
		Order order = findByOrderId(orderId);
		validateOrderComplete(order);
		validateOrderAndReviewUser(order, user);
		findExistingReview(order, user);
		Review review = reviewJpaRepository.save(Review.create(request, user, order));
		order.getStore().addStoreGrade(review.getGrade());

		return new ReviewResponse(
			review.getId(),
			review.getContent(),
			review.getGrade());
	}

	@Transactional(readOnly = true)
	public Slice<ReviewCursorResponse> getCursorReview(String storeId, Pageable pageable) {
		Store store = storeService.findById(storeId);
		return reviewQueryRepository.getAllReviewInStore(store, pageable);

	}

	@Transactional(readOnly = true)
	public Slice<ReviewDetailsCursorResponse> getDetailsCursorUserReview(String userId, Pageable pageable){
		User user = userService.findById(userId);
		return reviewQueryRepository.getAllReviewInUser(user, pageable);
	}

	@Transactional
	public ReviewResponse updateReview(User user, String reviewId, ReviewRequest request){
		checkCustomerUserRole(user);
		Review review = findById(reviewId);
		validateOrderAndReviewUser(review.getOrder(), user);
		review.update(request);

		return new ReviewResponse(
			review.getId(),
			review.getContent(),
			review.getGrade());
	}

	@Transactional
	public void deleteReview(User user, String reviewId){
		checkDeleteReviewUserRole(user);
		Review review = findById(reviewId);
		deleteByRole(user, review);

	}

	public void deleteByRole(User user, Review review){
		if(user.getUserRole() == UserRole.CUSTOMER){
			validateOrderAndReviewUser(review.getOrder(), user);
			deleteByCustomer(review, user);
		} else {
			deleteByAdmin(review, user);
		}
	}

	public void deleteByCustomer(Review review, User user){
		review.delete(user.getId());
	}

	public void deleteByAdmin(Review review, User user){
		review.delete(user.getId());
	}

	public void checkCustomerUserRole(User user) {
		if (user.getUserRole() != UserRole.CUSTOMER) {
			throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_PERMISSIONS.getMessage());
		}
	}

	public void validateOrderComplete(Order order){
		if(order.getOrderStatus() != OrderStatus.COMPLETED){
			throw new IllegalArgumentException(ErrorCode.ORDER_INCOMPLETE_PROCESS.getMessage());
		}
	}

	public void checkDeleteReviewUserRole(User user){
		if(user.getUserRole() != UserRole.CUSTOMER
			&& user.getUserRole() != UserRole.MANAGER
			&& user.getUserRole() != UserRole.MASTER){
			throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_PERMISSIONS.getMessage());
		}
	}

	public void validateOrderAndReviewUser(Order order, User user){
		if(order.getUser().equals(user)){
			throw new IllegalArgumentException(ErrorCode.NOT_MATCH_CONFIRM.getMessage());
		}
	}

	public void findExistingReview(Order order, User user){
		if(reviewJpaRepository.existsByUserIdAndOrderId(user.getId(), order.getId())){
			throw new IllegalArgumentException(ErrorCode.EXISTING_REVIEW.getMessage());
		}
	}



	private Order findByOrderId(String orderId) {
		return orderJpaRepository.findById(UUID.fromString(orderId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));
	}



	public Review findById(String reviewId){
		return reviewJpaRepository.findById(UUID.fromString(reviewId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_REVIEW.getMessage()));
	}




}
