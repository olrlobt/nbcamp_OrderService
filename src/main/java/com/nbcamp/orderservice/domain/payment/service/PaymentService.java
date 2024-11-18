package com.nbcamp.orderservice.domain.payment.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.order.entity.Order;
import com.nbcamp.orderservice.domain.order.repository.OrderJpaRepository;
import com.nbcamp.orderservice.domain.payment.dto.PaymentRequest;
import com.nbcamp.orderservice.domain.payment.dto.PaymentResponse;
import com.nbcamp.orderservice.domain.payment.entity.Payment;
import com.nbcamp.orderservice.domain.payment.repository.PaymentJpaRepository;
import com.nbcamp.orderservice.domain.payment.repository.PaymentQueryRepository;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentJpaRepository paymentJpaRepository;
	private final PaymentQueryRepository paymentQueryRepository;
	private final OrderJpaRepository orderJpaRepository;

	@Transactional
	public PaymentResponse createPayment(String orderId, PaymentRequest request, User user) {
		Order order = getOrder(orderId);
		// 외부 결제 연동 로직. 그에 따른 PaymentStatus 제어
		Payment payment = Payment.create(order, user, request);
		paymentJpaRepository.save(payment);
		return getPaymentResponse(payment);
	}

	@Transactional(readOnly = true)
	public Slice<PaymentResponse> getAllPaymentsByOrderId(String orderId, User user, Pageable pageable,
		SortOption sortOption) {
		return paymentQueryRepository.getAllPaymentsByOrderId(UUID.fromString(orderId), user, pageable, sortOption);
	}

	@Transactional(readOnly = true)
	public PaymentResponse getPayment(String orderId, String paymentId, User user) {
		Order order = getOrder(orderId);
		verifyUserOrder(order, user);
		Payment payment = getPayment(paymentId);
		return getPaymentResponse(payment);
	}

	@Transactional
	public void deleteProduct(String orderId, String paymentId, User user) {
		Order order = getOrder(orderId);
		verifyUserOrder(order, user);
		validateOwner(user.getUserRole());
		Payment payment = getPayment(paymentId);
		payment.delete(user.getId());
	}

	private void verifyUserOrder(Order order, User user) {
		if (!order.getUser().equals(user)) {
			throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_PERMISSION.getMessage());
		}
	}

	private Order getOrder(String orderId) {
		return orderJpaRepository.findById(UUID.fromString(orderId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));
	}

	private static PaymentResponse getPaymentResponse(Payment payment) {
		return new PaymentResponse(payment.getId(), payment.getPaymentStatus(), payment.getPaymentMethod(),
			payment.getAmount());
	}

	private void validateOwner(UserRole role) {
		if (!role.equals(UserRole.MANAGER) && !role.equals(UserRole.MASTER)) {
			throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_PERMISSIONS.getMessage());
		}
	}

	private Payment getPayment(String paymentId) {
		return paymentJpaRepository.findById(UUID.fromString(paymentId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_PAYMENT.getMessage()));
	}

}
