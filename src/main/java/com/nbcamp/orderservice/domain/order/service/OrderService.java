package com.nbcamp.orderservice.domain.order.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.order.dto.OrderInfoDto;
import com.nbcamp.orderservice.domain.order.dto.OrderProductResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderRequest;
import com.nbcamp.orderservice.domain.order.dto.OrderResponse;
import com.nbcamp.orderservice.domain.order.entity.Order;
import com.nbcamp.orderservice.domain.order.entity.OrderProduct;
import com.nbcamp.orderservice.domain.order.repository.OrderProductRepository;
import com.nbcamp.orderservice.domain.order.repository.OrderQueryRepository;
import com.nbcamp.orderservice.domain.order.repository.OrderRepository;
import com.nbcamp.orderservice.domain.product.entity.Product;
import com.nbcamp.orderservice.domain.product.repository.ProductJpaRepository;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.store.repository.StoreJpaRepository;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;
	private final StoreJpaRepository storeJpaRepository;
	private final ProductJpaRepository productJpaRepository;
	private final OrderQueryRepository orderQueryRepository;

	@Transactional
	public OrderInfoDto createOrder(OrderRequest request, User user) {
		Store store = getStoreById(request.storeId());
		validateUserRoleForCreateOrder(user);

		Order order = Order.create(request, store, user);
		orderRepository.save(order);

		List<Product> products = getProductsFromRequest(request.products());
		List<OrderProduct> orderProducts = OrderProduct.create(order, products, request.products());
		orderProductRepository.saveAll(orderProducts);

		OrderResponse orderResponse = new OrderResponse(
			order.getId(),
			order.getStore().getId(),
			order.getUser().getId(),
			order.getOrderStatus(),
			order.getOrderType(),
			order.getDeliveryAddress(),
			order.getRequest(),
			order.getTotalPrice()
		);

		List<OrderProductResponse> orderProductResponses = orderProducts.stream()
			.map(orderProduct -> new OrderProductResponse(
				orderProduct.getId(),
				orderProduct.getOrder().getId(),
				orderProduct.getProduct().getId(),
				orderProduct.getProduct().getName(),
				orderProduct.getQuantity(),
				orderProduct.getTotalPrice()
			))
			.toList();

		return new OrderInfoDto(orderResponse, orderProductResponses);
	}

	public Page<OrderInfoDto> getOrders(Pageable pageable, User user, String query) {
		UserRole userRole = user.getUserRole();

		if (userRole == UserRole.OWNER) {
			Store store = storeJpaRepository.findByUser(user)
				.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_STORE.getMessage()));
			return orderQueryRepository.findByStore(store, pageable, query);
		} else if (userRole == UserRole.CUSTOMER) {
			return orderQueryRepository.findByUser(user, pageable, query);
		} else {
			return orderQueryRepository.findAllOrders(pageable, query);
		}
	}

	private Store getStoreById(UUID storeId) {
		return storeJpaRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_STORE.getMessage()));
	}

	private void validateUserRoleForCreateOrder(User user) {
		if (user.getUserRole() != UserRole.CUSTOMER && user.getUserRole() != UserRole.OWNER) {
			throw new IllegalArgumentException(ErrorCode.NO_PERMISSION_TO_CREATE_ORDER.getMessage());
		}
	}

	private Product getProductById(String productId) {
		return productJpaRepository.findById(UUID.fromString(productId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_PRODUCT.getMessage()));
	}

	private List<Product> getProductsFromRequest(List<OrderRequest.OrderProduct> productRequests) {
		return productRequests.stream()
			.map(productRequest -> getProductById(productRequest.productId().toString()))
			.collect(Collectors.toList());
	}

	@Transactional
	public void cancelOrder(String orderId, User user) {
		Order order = orderRepository.findById(UUID.fromString(orderId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));

		if (order.getDeletedAt() != null) {
			throw new IllegalStateException(ErrorCode.ALREADY_CANCELED.getMessage());
		}

		if (user.getUserRole() == UserRole.CUSTOMER) {
			LocalDateTime orderTime = order.getCreatedAt();
			LocalDateTime now = LocalDateTime.now();
			if (Duration.between(orderTime, now).toMinutes() > 5) {
				throw new IllegalStateException(ErrorCode.CANCELLATION_TIME_EXCEEDED.getMessage());
			}
		}

		order.cancelOrder(user.getId());
	}

	public OrderInfoDto getOrderDetail(UUID orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));

		List<OrderProduct> orderProducts = orderQueryRepository.findOrderProductsBy(orderId);

		OrderResponse orderResponse = new OrderResponse(
			order.getId(),
			order.getStore().getId(),
			order.getUser().getId(),
			order.getOrderStatus(),
			order.getOrderType(),
			order.getDeliveryAddress(),
			order.getRequest(),
			order.getTotalPrice()
		);

		List<OrderProductResponse> orderProductResponses = orderProducts.stream()
			.map(orderProduct -> new OrderProductResponse(
				orderProduct.getId(),
				orderProduct.getOrder().getId(),
				orderProduct.getProduct().getId(),
				orderProduct.getProduct().getName(),
				orderProduct.getQuantity(),
				orderProduct.getTotalPrice()
			))
			.toList();

		return new OrderInfoDto(orderResponse, orderProductResponses);
	}

	public OrderResponse updateOrderStatus(UUID orderId, OrderStatus newStatus) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));

		validateOrderStatus(order.getOrderStatus(), newStatus);

		order.updateOrderStatus(newStatus);
		orderRepository.save(order);

		// 업데이트된 OrderResponse 반환
		return new OrderResponse(
			order.getId(),
			order.getStore().getId(),
			order.getUser().getId(),
			order.getOrderStatus(),
			order.getOrderType(),
			order.getDeliveryAddress(),
			order.getRequest(),
			order.getTotalPrice()
		);
	}

	private void validateOrderStatus(OrderStatus currentStatus, OrderStatus newStatus) {
		if (currentStatus == OrderStatus.COMPLETED || currentStatus == OrderStatus.CANCELLED) {
			throw new IllegalArgumentException(ErrorCode.INVALID_ORDER_STATUS.getMessage());
		}
	}

}