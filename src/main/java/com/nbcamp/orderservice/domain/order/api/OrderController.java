package com.nbcamp.orderservice.domain.order.api;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.OrderType;
import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.order.dto.OrderInfoResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderProductResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderRequest;
import com.nbcamp.orderservice.domain.order.dto.OrderResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderUpdateRequest;
import com.nbcamp.orderservice.domain.order.service.OrderService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = {"/api/v1"})
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/orders")
	public ResponseEntity<CommonResponse<OrderInfoResponse>> createOrder(
		@RequestBody OrderRequest request,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT,
			orderService.createOrder(request, userDetails.getUser()));
	}

	@PreAuthorize("hasAnyRole('OWNER','MANAGER','MASTER')")
	@GetMapping("/stores/{storeId}/orders")
	public ResponseEntity<CommonResponse<Page<OrderResponse>>> getAllOrdersAdmin(
		Pageable pageable,
		@PathVariable UUID storeId,
		@RequestParam(value = "orderType", required = false) OrderType orderType,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		@RequestParam(value = "orderStatus", required = false) OrderStatus orderStatus,
		@RequestParam(value = "sortOption", required = false, defaultValue = "CREATED_AT_ASC") SortOption sortOption
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			orderService.getOrdersAdmin(
				pageable,
				storeId,
				orderType,
				startDate,
				endDate,
				orderStatus,
				sortOption
			));
	}

	@PreAuthorize("hasAnyRole('CUSTOMER')")
	@GetMapping("/orders")
	public ResponseEntity<CommonResponse<Page<OrderResponse>>> getAllOrdersCustomer(
		Pageable pageable,
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam(required = false) String storeName,
		@RequestParam(required = false) UUID categoryId,
		@RequestParam(value = "orderType", required = false) OrderType orderType,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		@RequestParam(value = "orderStatus", required = false) OrderStatus orderStatus,
		@RequestParam(value = "sortOption", required = false, defaultValue = "CREATED_AT_ASC") SortOption sortOption
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			orderService.getOrdersCustomer(
				pageable,
				userDetails.getUser(),
				storeName,
				categoryId,
				orderType,
				startDate,
				endDate,
				orderStatus,
				sortOption
			));
	}

	@GetMapping("/orders/{orderId}")
	public ResponseEntity<CommonResponse<List<OrderProductResponse>>> getOrderDetail(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID orderId
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			orderService.getOrderDetail(orderId, userDetails.getUser()));
	}

	@PreAuthorize("hasAnyRole('OWNER','MANAGER','MASTER')")
	@PutMapping("/orders/{orderId}")
	public ResponseEntity<CommonResponse<OrderResponse>> updateOrderStatus(
		@PathVariable UUID orderId,
		@RequestBody @Valid OrderUpdateRequest orderUpdateRequest,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_UPDATE,
			orderService.updateOrderStatus(orderId, orderUpdateRequest, userDetails.getUser()));
	}

	@DeleteMapping("/orders/{orderId}")
	public ResponseEntity<CommonResponse<Void>> cancelOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable String orderId) {
		orderService.cancelOrder(orderId, userDetails.getUser());
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}
}
