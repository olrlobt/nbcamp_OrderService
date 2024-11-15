package com.nbcamp.orderservice.domain.order.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.category.repository.CategoryJpaRepository;
import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.OrderType;
import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.order.dto.OrderInfoResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderProductResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderRequest;
import com.nbcamp.orderservice.domain.order.dto.OrderResponse;
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
	private final ProductJpaRepository productJpaRepository;
	private final OrderQueryRepository orderQueryRepository;
	private final OrderProductService orderProductService;
	private final CategoryJpaRepository categoryJpaRepository;

	@Transactional
	public OrderInfoResponse createOrder(OrderRequest request, User user) {
		validateUserRoleForCreateOrder(user);
		if(request.address() != null){
			validateAddressPattern(request.address());
		}
		Store store = getStoreById(request.storeId());

		Order order = Order.create(request, store, user);

		List<OrderProduct> orderProducts = orderProductService.createOrderProducts(order, request.products());
		order.addOrderProduct(orderProducts);
		orderJpaRepository.save(order);

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
				orderProduct.getProduct().getId(),
				orderProduct.getProduct().getName(),
				orderProduct.getQuantity(),
				orderProduct.getTotalPrice()
			))
			.toList();

		return new OrderInfoResponse(orderResponse, orderProductResponses);
	}


	@Transactional(readOnly = true)
	public Page<OrderResponse> getOrdersAdmin(
		Pageable pageable,
		UUID storeId,
		OrderType orderType,
		LocalDate startDate,
		LocalDate endDate,
		OrderStatus orderStatus,
		SortOption sortOption,
		User user
	) {
		if(user.getUserRole() == UserRole.OWNER){
			existsByStore(storeId, user.getId());
		}

		return orderQueryRepository.findByStoreOrders(
			pageable,
			storeId,
			orderType,
			startDate,
			endDate,
			orderStatus,
			sortOption
		);
	}

	@Transactional(readOnly = true)
	public Page<OrderResponse> getOrdersCustomer(
		Pageable pageable,
		User user,
		String storeName,
		UUID categoryId,
		OrderType orderType,
		LocalDate startDate,
		LocalDate endDate,
		OrderStatus orderStatus,
		SortOption sortOption
	) {
		if(categoryId != null){
			existsByCategory(categoryId);
		}
		return orderQueryRepository.findAllByUserOrder(
			pageable,
			user,
			storeName,
			categoryId,
			orderType,
			startDate,
			endDate,
			orderStatus,
			sortOption
		);
	}

	@Transactional(readOnly = true)
	public List<OrderProductResponse> getOrderDetail(UUID orderId, User user) {
		if(user.getUserRole() == UserRole.CUSTOMER){
			validateOrderInCustomer(orderId, user);
		}
		return orderQueryRepository.findAllByOrderProductInOrder(orderId);
	}


	@Transactional
	public OrderResponse updateOrderStatus(UUID orderId, OrderUpdateRequest orderUpdateRequest, User user) {
		Order order = findByOrder(orderId);
		if(user.getUserRole() == UserRole.OWNER){
			existsByStore(order.getStore().getId(), user.getId());
		}
		validateOrderStatus(order.getOrderStatus());

		if(orderUpdateRequest.deliveryAddress() != null){
			validateAddressPattern(orderUpdateRequest.deliveryAddress());
		}

		order.update(orderUpdateRequest);

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


	@Transactional
	public void cancelOrder(UUID orderId, User user) {
		Order order = findByOrder(orderId);

		if (order.getDeletedAt() != null) {
			throw new IllegalStateException(ErrorCode.ALREADY_CANCELED.getMessage());
		}

		if (user.getUserRole() == UserRole.CUSTOMER) {
			validateOrderCreateAtDuration(order.getCreatedAt(), LocalDateTime.now());
		}

		order.cancelOrder(user.getId());
	}


	private void validateAddressPattern(String fullAddress) {
		String addressPattern = "([가-힣]+[특별시|광역시|도])\\s([가-힣]+구)";
		Pattern pattern = Pattern.compile(addressPattern);
		Matcher matcher = pattern.matcher(fullAddress);
		if (matcher.find()) {
			return;
		}
		throw new IllegalArgumentException(ErrorCode.ADDRESS_PATTERN_MISMATCH.getMessage());
	}

	private void validateOrderCreateAtDuration(LocalDateTime orderTime, LocalDateTime now){
		if (Duration.between(orderTime, now).toMinutes() > 5) {
			throw new IllegalStateException(ErrorCode.CANCELLATION_TIME_EXCEEDED.getMessage());
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


	private void validateOrderInCustomer(UUID orderId, User user){
		if(!orderJpaRepository.existsByIdAndUserId(orderId, user.getId())){
			throw new IllegalArgumentException(ErrorCode.NOT_MATCH_CONFIRM.getMessage());
		}
	}

	private void validateOrderStatus(OrderStatus currentStatus) {
		if (currentStatus == OrderStatus.COMPLETED || currentStatus == OrderStatus.CANCELLED) {
			throw new IllegalArgumentException(ErrorCode.INVALID_ORDER_STATUS.getMessage());
		}
	}

	private Order findByOrder(UUID orderId){
		return orderJpaRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));
	}

	private void existsByStore(UUID storeId, UUID userId){
		if(!storeJpaRepository.existsByIdAndUserId(storeId, userId)){
			throw new IllegalArgumentException(ErrorCode.NOT_MATCH_CONFIRM.getMessage());
		}
	}

	private void existsByCategory(UUID categoryId){
		if(!categoryJpaRepository.existsById(categoryId)){
			throw new IllegalArgumentException(ErrorCode.NOT_FOUND_CATEGORY.getMessage());
		}
	}

}
