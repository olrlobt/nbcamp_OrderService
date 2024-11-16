package com.nbcamp.orderservice.domain.payment.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.payment.dto.PaymentRequest;
import com.nbcamp.orderservice.domain.payment.dto.PaymentResponse;
import com.nbcamp.orderservice.domain.payment.service.PaymentService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = {"/v1/api/orders"})
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/{orderId}/payments")
	public ResponseEntity<CommonResponse<PaymentResponse>> createPayment(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("orderId") String orderId,
		@RequestBody PaymentRequest request
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT,
			paymentService.createPayment(orderId, request, userDetails.getUser()));
	}

	@GetMapping("/{orderId}/payments")
	public ResponseEntity<CommonResponse<Slice<PaymentResponse>>> getAllPaymentsByOrderId(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("orderId") String orderId,
		@RequestParam(value = "sortOption", required = false, defaultValue = "CREATED_AT_ASC") SortOption sortOption,
		Pageable pageable
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			paymentService.getAllPaymentsByOrderId(orderId, userDetails.getUser(), pageable, sortOption));
	}

	@GetMapping("/{orderId}/payments/{paymentId}")
	public ResponseEntity<CommonResponse<PaymentResponse>> getPayment(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("orderId") String orderId,
		@PathVariable("paymentId") String paymentId
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			paymentService.getPayment(orderId, paymentId, userDetails.getUser()));
	}

	@DeleteMapping("/{orderId}/payments/{paymentId}")
	public ResponseEntity<CommonResponse<Void>> deleteProduct(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("orderId") String orderId,
		@PathVariable("paymentId") String paymentId
	) {
		paymentService.deleteProduct(orderId, paymentId, userDetails.getUser());
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}

}
