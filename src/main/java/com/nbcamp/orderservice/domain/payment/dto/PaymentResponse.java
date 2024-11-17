package com.nbcamp.orderservice.domain.payment.dto;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.PaymentMethod;
import com.nbcamp.orderservice.domain.common.PaymentStatus;

public record PaymentResponse(
	UUID paymentId,
	PaymentStatus paymentStatus,
	PaymentMethod paymentMethod,
	int amount
) {
}
