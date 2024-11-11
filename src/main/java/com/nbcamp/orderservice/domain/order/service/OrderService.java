package com.nbcamp.orderservice.domain.order.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.order.dto.OrderRequest;
import com.nbcamp.orderservice.domain.order.dto.OrderResponse;
import com.nbcamp.orderservice.domain.order.entity.Order;
import com.nbcamp.orderservice.domain.order.entity.OrderProduct;
import com.nbcamp.orderservice.domain.order.repository.OrderProductRepository;
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

	@Transactional
	public OrderResponse createOrder(OrderRequest request, User user) {
		Store store = getStoreById(request.storeId());
		validateUserRoleForCreateOrder(user);

		Order order = Order.create(request, store, user);
		orderRepository.save(order);

		List<Product> products = getProductsFromRequest(request.products());
		List<OrderProduct> orderProducts = OrderProduct.create(order, products, request.products());
		orderProductRepository.saveAll(orderProducts);

		return new OrderResponse(
			order.getId(),
			order.getOrderStatus(),
			order.getStore().getId(),
			order.getOrderType(),
			order.getDeliveryAddress(),
			order.getRequest(),
			order.getTotalPrice(),
			orderProducts.stream()
				.map(orderProduct -> new OrderResponse.OrderProductResponse(
					orderProduct.getId(),
					orderProduct.getProduct().getId(),
					orderProduct.getQuantity(),
					orderProduct.getTotalPrice()
				))
				.collect(Collectors.toList())
		);
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
}