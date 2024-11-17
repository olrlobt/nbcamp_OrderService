package com.nbcamp.orderservice.domain.order.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.category.repository.CategoryJpaRepository;
import com.nbcamp.orderservice.domain.common.DisplayStatus;
import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.order.dto.OrderInfoResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderProductResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderRequest;
import com.nbcamp.orderservice.domain.order.dto.OrderResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderSearchAdminRequest;
import com.nbcamp.orderservice.domain.order.dto.OrderSearchCustomerRequest;
import com.nbcamp.orderservice.domain.order.dto.OrderUpdateRequest;
import com.nbcamp.orderservice.domain.order.entity.Order;
import com.nbcamp.orderservice.domain.order.entity.OrderProduct;
import com.nbcamp.orderservice.domain.order.repository.OrderJpaRepository;
import com.nbcamp.orderservice.domain.order.repository.OrderQueryRepository;
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

	private final OrderJpaRepository orderJpaRepository;
	private final StoreJpaRepository storeJpaRepository;
	private final OrderQueryRepository orderQueryRepository;
	private final OrderProductService orderProductService;
	private final CategoryJpaRepository categoryJpaRepository;
	private final ProductJpaRepository productJpaRepository;

	@Transactional
	public OrderInfoResponse createOrder(OrderRequest request, User user) {

		List<UUID> productIds = request.products().stream()
			.map(OrderRequest.OrderProduct::productId)
			.toList();

		validateProducts(productJpaRepository.findAllById(productIds), productIds, request.storeId());

		Store store = getStoreById(request.storeId());
		Order order = Order.create(request, store, user);

		List<OrderProduct> orderProducts = orderProductService.createOrderProducts(order, request.products());
		order.addOrderProduct(orderProducts);
		orderJpaRepository.save(order);

		OrderResponse orderResponse = new OrderResponse(order);

		List<OrderProductResponse> orderProductResponses =
			orderProducts.stream()
				.map(OrderProductResponse::new)
				.toList();

		return new OrderInfoResponse(orderResponse, orderProductResponses);
	}

	@Transactional(readOnly = true)
	public Page<OrderResponse> getOrdersAdmin(
		Pageable pageable,
		UUID storeId,
		OrderSearchAdminRequest request,
		User user
	) {
		if (user.getUserRole() == UserRole.OWNER) {
			existsByStore(storeId, user.getId());
		}

		return orderQueryRepository.findByStoreOrders(pageable, storeId, request);
	}

	@Transactional(readOnly = true)
	public Page<OrderResponse> getOrdersCustomer(
		Pageable pageable,
		User user,
		OrderSearchCustomerRequest request
	) {
		if (request.categoryId() != null) {
			existsByCategory(request.categoryId());
		}
		return orderQueryRepository.findAllByUserOrder(
			pageable,
			user,
			request
		);
	}

	@Transactional(readOnly = true)
	public List<OrderProductResponse> getOrderDetail(UUID orderId, User user) {
		if (user.getUserRole() == UserRole.CUSTOMER) {
			validateOrderInCustomer(orderId, user);
		}
		return orderQueryRepository.findAllByOrderProductInOrder(orderId);
	}

	@Transactional
	public OrderResponse updateOrderStatus(UUID orderId, OrderUpdateRequest orderUpdateRequest, User user) {
		Order order = findByOrder(orderId);
		if (user.getUserRole() == UserRole.OWNER) {
			existsByStore(order.getStore().getId(), user.getId());
		}
		if (user.getUserRole() == UserRole.CUSTOMER) {
			validateOrderStatusInCustomer(order.getOrderStatus());
		}
		validateOrderStatus(orderUpdateRequest.orderStatus(), order.getOrderStatus());

		order.update(orderUpdateRequest);

		return new OrderResponse(order);
	}

	@Transactional
	public void cancelOrder(UUID orderId, User user) {
		Order order = findByOrder(orderId);

		if (order.getDeletedAt() != null || order.getDeletedBy() != null) {
			throw new IllegalStateException(ErrorCode.ALREADY_CANCELED.getMessage());
		}

		if (user.getUserRole() == UserRole.CUSTOMER) {
			validateOrderCreateAtDuration(order.getCreatedAt(), LocalDateTime.now());
			validateOrderInCustomer(orderId, user);
		}

		order.cancelOrder(user.getId());
	}

	private void validateOrderCreateAtDuration(LocalDateTime orderTime, LocalDateTime now) {
		if (Duration.between(orderTime, now).toMinutes() > 5) {
			throw new IllegalStateException(ErrorCode.CANCELLATION_TIME_EXCEEDED.getMessage());
		}
	}

	private Store getStoreById(UUID storeId) {
		return storeJpaRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_STORE.getMessage()));
	}

	private void validateOrderInCustomer(UUID orderId, User user) {
		if (!orderJpaRepository.existsByIdAndUserId(orderId, user.getId())) {
			throw new IllegalArgumentException(ErrorCode.NOT_MATCH_CONFIRM.getMessage());
		}
	}

	private void validateProducts(List<Product> products, List<UUID> productIds, UUID storeId) {

		if (products.size() != productIds.size()) {
			throw new IllegalArgumentException(ErrorCode.PARTIALLY_NOT_FOUND_PRODUCT.getMessage());
		}
		for (Product product : products) {
			if (product.getDisplayStatus() != DisplayStatus.EXPOSED) {
				throw new IllegalArgumentException(ErrorCode.PRODUCT_NOT_AVAILABLE.getMessage());
			}
			if (!product.getStore().getId().equals(storeId)) {
				throw new IllegalArgumentException(ErrorCode.INVALID_PRODUCT_STORE_RELATION.getMessage());
			}
		}
	}

	private void validateOrderStatus(OrderStatus newStatus, OrderStatus currentStatus) {

		if (currentStatus == OrderStatus.ACCEPTED && newStatus == OrderStatus.PENDING) {
			throw new IllegalArgumentException(ErrorCode.ORDER_STATUS_ACCEPTED_DROP_INVALID.getMessage());
		} else if (currentStatus == OrderStatus.DELIVERING) {
			if (newStatus == OrderStatus.ACCEPTED || newStatus == OrderStatus.PENDING) {
				throw new IllegalArgumentException(ErrorCode.ORDER_STATUS_DELIVERING_DROP_INVALID.getMessage());
			}
		} else if (currentStatus == OrderStatus.COMPLETED || currentStatus == OrderStatus.CANCELLED) {
			throw new IllegalArgumentException(ErrorCode.INVALID_ORDER_STATUS.getMessage());
		}
	}

	private void validateOrderStatusInCustomer(OrderStatus currentStatus) {
		if (currentStatus == OrderStatus.COMPLETED || currentStatus == OrderStatus.CANCELLED) {
			throw new IllegalArgumentException(ErrorCode.INVALID_ORDER_STATUS.getMessage());
		}
	}

	private Order findByOrder(UUID orderId) {
		return orderJpaRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));
	}

	private void existsByStore(UUID storeId, UUID userId) {
		if (!storeJpaRepository.existsByIdAndUserId(storeId, userId)) {
			throw new IllegalArgumentException(ErrorCode.NOT_MATCH_CONFIRM.getMessage());
		}
	}

	private void existsByCategory(UUID categoryId) {
		if (!categoryJpaRepository.existsById(categoryId)) {
			throw new IllegalArgumentException(ErrorCode.NOT_FOUND_CATEGORY.getMessage());
		}
	}

}
