package com.nbcamp.orderservice.domain.payment.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
