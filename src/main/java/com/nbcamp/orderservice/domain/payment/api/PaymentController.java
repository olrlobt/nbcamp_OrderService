package com.nbcamp.orderservice.domain.payment.api;

import java.util.UUID;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = {"/api/v1/orders"})
@RequiredArgsConstructor
@Tag(name = "결제 관련 API")
public class PaymentController {

	private final PaymentService paymentService;

	@Operation(summary = "결제 생성")
	@PostMapping("/{orderId}/payments")
	public ResponseEntity<CommonResponse<PaymentResponse>> createPayment(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("orderId") UUID orderId,
		@RequestBody PaymentRequest request
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT,
			paymentService.createPayment(orderId, request, userDetails.getUser()));
	}

	@Operation(summary = "주문별 결제 목록 조회")
	@GetMapping("/{orderId}/payments")
	public ResponseEntity<CommonResponse<Slice<PaymentResponse>>> getAllPaymentsByOrderId(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("orderId") UUID orderId,
		@RequestParam(value = "sortOption", required = false, defaultValue = "CREATED_AT_ASC") SortOption sortOption,
		Pageable pageable
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			paymentService.getAllPaymentsByOrderId(orderId, userDetails.getUser(), pageable, sortOption));
	}

	@Operation(summary = "결제 상세 조회")
	@GetMapping("/{orderId}/payments/{paymentId}")
	public ResponseEntity<CommonResponse<PaymentResponse>> getPayment(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("orderId") UUID orderId,
		@PathVariable("paymentId") UUID paymentId
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			paymentService.getPayment(orderId, paymentId, userDetails.getUser()));
	}

	@Operation(summary = "결제 내역 삭제")
	@DeleteMapping("/{orderId}/payments/{paymentId}")
	public ResponseEntity<CommonResponse<Void>> deletePayment(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("orderId") UUID orderId,
		@PathVariable("paymentId") UUID paymentId
	) {
		paymentService.deletePayment(orderId, paymentId, userDetails.getUser());
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}

}
