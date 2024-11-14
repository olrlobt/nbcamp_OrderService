package com.nbcamp.orderservice.domain.order.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.nbcamp.orderservice.domain.order.dto.OrderInfoDto;
import com.nbcamp.orderservice.domain.order.dto.OrderProductResponse;
import com.nbcamp.orderservice.domain.order.dto.OrderResponse;
import com.nbcamp.orderservice.domain.order.entity.OrderProduct;
import com.nbcamp.orderservice.domain.order.entity.QOrder;
import com.nbcamp.orderservice.domain.order.entity.QOrderProduct;
import com.nbcamp.orderservice.domain.product.entity.QProduct;
import com.nbcamp.orderservice.domain.store.entity.QStore;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.user.entity.QUser;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
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
	QStore store = QStore.store;
	QUser user = QUser.user;
	QProduct product = QProduct.product;

	public Page<OrderInfoDto> findAllOrders(Pageable pageable, String query) {
		OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(pageable);

		List<OrderResponse> orderResponses = jpaQueryFactory
			.select(
				Projections.constructor(
					OrderResponse.class,
					order.id,
					order.orderStatus,
					order.store.id,
					order.orderType,
					order.deliveryAddress,
					order.request,
					order.totalPrice
				)
			)
			.from(order)
			.leftJoin(order.store)
			.leftJoin(order.user)
			.leftJoin(orderProduct).on(orderProduct.order.eq(order))
			.leftJoin(orderProduct.product).fetchJoin()
			.where(
				storeNameLike(query),
				productNameLike(query)
			)
			.orderBy(orderSpecifiers)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(order.count())
			.from(order)
			.leftJoin(orderProduct).on(orderProduct.order.eq(order))
			.leftJoin(orderProduct.product)
			.where(
				storeNameLike(query),
				productNameLike(query)
			)
			.fetchOne();

		return getOrderInfo(pageable, orderResponses, total);

	}

	public Page<OrderInfoDto> findByStore(Store store, Pageable pageable, String query) {
		OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(pageable);

		List<OrderResponse> orderResponses = jpaQueryFactory
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
					order.totalPrice
				)
			)
			.from(order)
			.leftJoin(order.user)
			.where(
				order.store.eq(store),
				storeNameLike(query),
				productNameLike(query)
			)
			.orderBy(orderSpecifiers)  // 정렬 조건
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(order.count())
			.from(order)
			.where(
				order.store.eq(store),
				storeNameLike(query),
				productNameLike(query)
			)
			.fetchOne();

		return getOrderInfo(pageable, orderResponses, total);
	}

	public Page<OrderInfoDto> findByUser(User user, Pageable pageable, String query) {
		OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(pageable);

		List<OrderResponse> orderResponses = jpaQueryFactory
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
					order.totalPrice
				)
			)
			.from(order)
			.join(order.store, store)
			.where(order.user.eq(user),
				storeNameLike(query),
				JPAExpressions.selectOne()
					.from(orderProduct)
					.innerJoin(orderProduct.product, product)
					.where(
						orderProduct.order.id.eq(order.id),
						productNameLike(query)
					).exists()
			)
			.orderBy(orderSpecifiers)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = jpaQueryFactory
			.select(order.count())
			.from(order)
			.where(order.user.eq(user),
				storeNameLike(query),
				productNameLike(query))
			.fetchOne();

		log.info("total :" + total);

		return getOrderInfo(pageable, orderResponses, total);
	}

	private Page<OrderInfoDto> getOrderInfo(Pageable pageable, List<OrderResponse> orderResponses,
		Long total) {

		List<UUID> orderIds = orderResponses.stream()
			.map(OrderResponse::orderId)
			.collect(Collectors.toList());
		List<OrderProductResponse> orderProductResponses = jpaQueryFactory
			.select(
				Projections.constructor(
					OrderProductResponse.class,
					orderProduct.id,
					orderProduct.order.id,
					orderProduct.product.id,
					orderProduct.product.name,
					orderProduct.quantity,
					orderProduct.totalPrice
				)
			)
			.distinct()
			.from(orderProduct)
			.where(orderProduct.order.id.in(orderIds))
			.fetch();

		Map<UUID, List<OrderProductResponse>> productsGroupedByOrderId = orderProductResponses.stream()
			.collect(Collectors.groupingBy(OrderProductResponse::orderId));

		List<OrderInfoDto> orderInfos = orderResponses.stream()
			.map(orderResponse -> {
				List<OrderProductResponse> productsForOrder = productsGroupedByOrderId.getOrDefault(
					orderResponse.orderId(),
					Collections.emptyList()
				);
				return new OrderInfoDto(orderResponse, productsForOrder);
			})
			.collect(Collectors.toList());

		return new PageImpl<>(orderInfos, pageable, total != null ? total : 0L);
	}

	private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
		String sortProperty =
			pageable.getSort().isEmpty() ? "createdAt" : pageable.getSort().iterator().next().getProperty();
		Sort.Direction direction =
			pageable.getSort().isEmpty() ? Sort.Direction.DESC : pageable.getSort().iterator().next().getDirection();

		OrderSpecifier<?> primaryOrderSpecifier;
		// 정렬 조건 설정
		if ("updatedAt".equalsIgnoreCase(sortProperty)) {
			primaryOrderSpecifier = direction.isAscending() ? order.updatedAt.asc() : order.updatedAt.desc();
		} else if ("user".equalsIgnoreCase(sortProperty)) {
			primaryOrderSpecifier = direction.isAscending() ? order.user.username.asc() : order.user.username.desc();
		} else if ("store".equalsIgnoreCase(sortProperty)) {
			primaryOrderSpecifier = direction.isAscending() ? order.store.name.asc() : order.store.name.desc();
		} else {
			primaryOrderSpecifier = direction.isAscending() ? order.createdAt.asc() : order.createdAt.desc();
		}

		return new OrderSpecifier[] {primaryOrderSpecifier, order.updatedAt.desc()};
	}

	private BooleanExpression storeNameLike(String query) {
		return StringUtils.hasText(query) ? order.store.name.containsIgnoreCase(query) : null;
	}

	private BooleanExpression productNameLike(String query) {
		return StringUtils.hasText(query) ? QOrderProduct.orderProduct.product.name.containsIgnoreCase(query) : null;
	}

	public List<OrderProduct> findOrderProductsBy(UUID orderId) {
		return jpaQueryFactory
			.selectFrom(orderProduct)
			.distinct()
			.where(orderProduct.order.id.eq(orderId))
			.fetch();
	}
}
