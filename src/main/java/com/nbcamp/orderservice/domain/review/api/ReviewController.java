package com.nbcamp.orderservice.domain.review.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.review.dto.ReviewCursorResponse;
import com.nbcamp.orderservice.domain.review.dto.ReviewRequest;
import com.nbcamp.orderservice.domain.review.dto.ReviewResponse;
import com.nbcamp.orderservice.domain.review.service.ReviewService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/orders/{orderId}/reviews")
	public ResponseEntity<CommonResponse<ReviewResponse>> createReview(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable String orderId,
		@RequestBody ReviewRequest reviewRequest
	){
		return CommonResponse.success(
			SuccessCode.SUCCESS_INSERT,
			reviewService.createReview(userDetails.getUser(), orderId, reviewRequest));
	}

	@GetMapping("/stores/{storeId}/orders/reviews")
	public ResponseEntity<CommonResponse<Slice<ReviewCursorResponse>>> getAllReview(
		@PathVariable String storeId,
		Pageable pageable
	){
		return CommonResponse.success(SuccessCode.SUCCESS, reviewService.findAllByStoreAndReview(storeId, pageable));
	}

}
