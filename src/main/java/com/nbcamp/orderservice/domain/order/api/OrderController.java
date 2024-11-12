package com.nbcamp.orderservice.domain.order.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.order.dto.OrderRequest;
import com.nbcamp.orderservice.domain.order.dto.OrderResponse;
import com.nbcamp.orderservice.domain.order.service.OrderService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = {"/api/v1"})
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/orders")
	public ResponseEntity<CommonResponse<OrderResponse>> createOrder(
		@RequestBody OrderRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT,
			orderService.createOrder(request, userDetails.getUser()));
	}

	@DeleteMapping("/orders/{orderId}")
	public ResponseEntity<CommonResponse<Void>> cancelOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable String orderId) {
		orderService.cancelOrder(orderId, userDetails.getUser());
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}

}