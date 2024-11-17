package com.nbcamp.orderservice.domain.payment.dto;

import com.nbcamp.orderservice.domain.common.PaymentMethod;

public record PaymentRequest(
	PaymentMethod paymentMethod,
	int amount
) {
}
