package com.nbcamp.orderservice.domain.order.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.nbcamp.orderservice.domain.common.OrderStatus;
import com.nbcamp.orderservice.domain.common.OrderType;
import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.order.dto.OrderProductResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderSearchAdminRequest;
import com.nbcamp.orderservice.domain.order.dto.OrderSearchCustomerRequest;
import com.nbcamp.orderservice.domain.order.entity.QOrder;
import com.nbcamp.orderservice.domain.order.entity.QOrderProduct;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class OrderQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;
	QOrder order = QOrder.order;
	QOrderProduct orderProduct = QOrderProduct.orderProduct;

	public Page<OrderResponse> findByStoreOrders(
		Pageable pageable,
		UUID storeId,
		OrderSearchAdminRequest request
	) {

		List<OrderResponse> orderResponseList = jpaQueryFactory.query()
			.select(
				Projections.constructor(
					OrderResponse.class,
					order.id,
					order.store.id,
					order.user.id,
					order.orderStatus,
					order.orderType,
					order.deliveryAddress,
					order.request,
					order.totalPrice,
					order.store.name
				)
			)
			.from(order)
			.where(
				order.store.id.eq(storeId),
				orderTypeEquals(request.orderType()),
				orderPeriodCondition(request.startDate(), request.endDate()),
				orderStateEquals(request.orderStatus())

			)
			.orderBy(getOrderSpecifier(request.sortOption()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = Optional.ofNullable(
			jpaQueryFactory
				.select(order.id.countDistinct())
				.from(order)
				.where(
					order.store.id.eq(storeId),
					orderTypeEquals(request.orderType()),
					orderPeriodCondition(request.startDate(), request.endDate()),
					orderStateEquals(request.orderStatus())
				)
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(orderResponseList, pageable, total);

	}

	public Page<OrderResponse> findAllByUserOrder(
		Pageable pageable,
		User user,
		OrderSearchCustomerRequest request
	) {
		List<OrderResponse> orderResponseList = jpaQueryFactory.query()
			.select(
				Projections.constructor(
					OrderResponse.class,
					order.id,
					order.store.id,
					order.user.id,
					order.orderStatus,
					order.orderType,
					order.deliveryAddress,
					order.request,
					order.totalPrice,
					order.store.name
				)
			)
			.from(order)
			.where(
				order.user.id.eq(user.getId()),
				storeNameContains(request.storeName()),
				categoryEquals(request.categoryId()),
				orderTypeEquals(request.orderType()),
				orderPeriodCondition(request.startDate(), request.endDate()),
				orderStateEquals(request.orderStatus())
			)
			.orderBy(getOrderSpecifier(request.sortOption()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = Optional.ofNullable(
			jpaQueryFactory
				.select(order.id.countDistinct())
				.from(order)
				.where(
					order.user.id.eq(user.getId()),
					storeNameContains(request.storeName()),
					categoryEquals(request.categoryId()),
					orderTypeEquals(request.orderType()),
					orderPeriodCondition(request.startDate(), request.endDate()),
					orderStateEquals(request.orderStatus())
				)
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(orderResponseList, pageable, total);

	}

	public List<OrderProductResponse> findAllByOrderProductInOrder(UUID orderId) {

		return jpaQueryFactory.query()
			.select(
				Projections.constructor(
					OrderProductResponse.class,
					orderProduct.id,
					orderProduct.product.id,
					orderProduct.product.name,
					orderProduct.quantity,
					orderProduct.totalPrice
				)
			).from(orderProduct)
			.join(orderProduct.order, order).on(order.id.eq(orderId))
			.orderBy(orderProduct.totalPrice.desc())
			.fetch();
	}

	private BooleanExpression storeNameContains(String storeName) {
		return StringUtils.hasText(storeName) ? order.store.name.containsIgnoreCase(storeName) : null;
	}

	private BooleanExpression categoryEquals(UUID categoryId) {
		return categoryId != null ? order.store.storeCategory.any().id.eq(categoryId) : null;
	}

	private BooleanExpression orderTypeEquals(OrderType orderType) {
		return orderType != null ? order.orderType.eq(orderType) : null;
	}

	private BooleanExpression orderPeriodCondition(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null) {
			return null;
		}
		return order.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
	}

	private BooleanExpression orderStateEquals(OrderStatus orderStatus) {
		return orderStatus != null ? order.orderStatus.eq(orderStatus) : null;
	}

	private OrderSpecifier<?> getOrderSpecifier(SortOption sortOption) {
		return switch (sortOption) {
			case CREATED_AT_DESC -> order.createdAt.desc();
			case UPDATED_AT_ASC -> order.updatedAt.asc();
			case UPDATED_AT_DESC -> order.updatedAt.desc();
			default -> order.createdAt.asc();
		};
	}

}
