package com.nbcamp.orderservice.domain.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nbcamp.orderservice.domain.order.entity.Order;
import com.nbcamp.orderservice.domain.order.repository.OrderJpaRepository;
import com.nbcamp.orderservice.domain.payment.dto.PaymentRequest;
import com.nbcamp.orderservice.domain.payment.dto.PaymentResponse;
import com.nbcamp.orderservice.domain.payment.entity.Payment;
import com.nbcamp.orderservice.domain.payment.repository.PaymentJpaRepository;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentJpaRepository paymentJpaRepository;
	private final OrderJpaRepository orderJpaRepository;

	public PaymentResponse createPayment(String orderId, PaymentRequest request, User user) {
		Order order = orderJpaRepository.findById(UUID.fromString(orderId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_ORDER.getMessage()));
		// 외부 결제 연동 로직. 그에 따른 PaymentStatus 제어
		Payment payment = Payment.create(order, user, request);
		paymentJpaRepository.save(payment);
		return new PaymentResponse(payment.getId(), payment.getPaymentStatus(), payment.getPaymentMethod(),
			payment.getAmount());
	}

}
