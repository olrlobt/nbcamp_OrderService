package com.nbcamp.orderservice.domain.order.api;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.order.dto.OrderInfoDto;
import com.nbcamp.orderservice.domain.order.dto.OrderRequest;
import com.nbcamp.orderservice.domain.order.dto.OrderResponse;
import com.nbcamp.orderservice.domain.order.service.OrderService;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;
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
	public ResponseEntity<CommonResponse<OrderInfoDto>> createOrder(
		@RequestBody OrderRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT,
			orderService.createOrder(request, userDetails.getUser()));
	}

	@GetMapping("/orders")
	public ResponseEntity<CommonResponse<Page<OrderInfoDto>>> getAllOrders(
		@RequestParam(value = "query", required = false) String query,
		@RequestParam(value = "sort", defaultValue = "createdAt") String sort, // 기본 정렬: 생성일
		@RequestParam(value = "direction", defaultValue = "desc") String direction, // 기본 정렬 순서: 내림차순
		@RequestParam(value = "size", defaultValue = "10") int size,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		if (size != 10 && size != 30 && size != 50) {
			size = 10; // 기본값으로 고정
		}
		;
		Sort sortCriteria = Sort.by(
			"desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sort
		);
		return CommonResponse.success(SuccessCode.SUCCESS,
			orderService.getOrders(PageRequest.of(page, size, sortCriteria), userDetails.getUser(), query));
	}

	@DeleteMapping("/orders/{orderId}")
	public ResponseEntity<CommonResponse<Void>> cancelOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable String orderId) {
		orderService.cancelOrder(orderId, userDetails.getUser());
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}

	@GetMapping("/orders/{orderId}")
	public ResponseEntity<CommonResponse<OrderInfoDto>> getOrderDetail(@PathVariable String orderId) {
		OrderInfoDto orderInfo = orderService.getOrderDetail(UUID.fromString(orderId));
		return CommonResponse.success(SuccessCode.SUCCESS, orderInfo);
	}

	@PatchMapping("orders/{orderId}")
	public ResponseEntity<CommonResponse<OrderResponse>> updateOrderStatus(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID orderId,
		@RequestParam String status) {
		if ((userDetails.getUser().getUserRole() == UserRole.CUSTOMER)) {
			throw new IllegalArgumentException(ErrorCode.ACCESS_DENIED.getMessage());
		}
		OrderStatus newStatus;
		try {
			newStatus = OrderStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(ErrorCode.COMMON_INVALID_PARAM.getMessage());
		}
		OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
		return CommonResponse.success(SuccessCode.SUCCESS_UPDATE, updatedOrder);
	}

}