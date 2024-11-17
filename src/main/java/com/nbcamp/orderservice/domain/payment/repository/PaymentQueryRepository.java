package com.nbcamp.orderservice.domain.payment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.payment.dto.PaymentResponse;
import com.nbcamp.orderservice.domain.payment.entity.QPayment;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	QPayment payment = QPayment.payment;

	public Slice<PaymentResponse> getAllPaymentsByOrderId(UUID orderId, User user, Pageable pageable,
		SortOption sortOption) {
		int pageSize = pageable.getPageSize();

		List<PaymentResponse> paymentList = jpaQueryFactory
			.select(
				Projections.constructor(
					PaymentResponse.class,
					payment.id,
					payment.paymentStatus,
					payment.paymentMethod,
					payment.amount
				)
			)
			.from(payment)
			.where(
				payment.user.eq(user),
				payment.deletedAt.isNull(),
				payment.deletedBy.isNull(),
				payment.order.id.eq(orderId)
			)
			.orderBy(getOrderSpecifier(sortOption, payment))
			.limit(pageSize + 1)
			.fetch();

		boolean hasNext = paymentList.size() > pageSize;
		if (hasNext) {
			paymentList.remove(pageSize);
		}

		return new SliceImpl<>(paymentList, pageable, hasNext);
	}

	private OrderSpecifier<?> getOrderSpecifier(SortOption sortOption, QPayment payment) {
		return switch (sortOption) {
			case CREATED_AT_DESC -> payment.createdAt.desc();
			case UPDATED_AT_ASC -> payment.updatedAt.asc();
			case UPDATED_AT_DESC -> payment.updatedAt.desc();
			default -> payment.createdAt.asc();
		};
	}
}
