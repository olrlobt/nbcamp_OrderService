package com.nbcamp.orderservice.domain.order.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.order.dto.OrderRequest;
import com.nbcamp.orderservice.domain.order.entity.Order;
import com.nbcamp.orderservice.domain.order.entity.OrderProduct;
import com.nbcamp.orderservice.domain.order.repository.OrderProductJpaRepository;
import com.nbcamp.orderservice.domain.product.entity.Product;
import com.nbcamp.orderservice.domain.product.repository.ProductJpaRepository;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderProductService {
	private final OrderProductJpaRepository orderProductJpaRepository;
	private final ProductJpaRepository productJpaRepository;

	@Transactional
	public List<OrderProduct> createOrderProducts(Order order, List<OrderRequest.OrderProduct> productList){
		List<OrderProduct> orderProducts = new ArrayList<>();
		for (OrderRequest.OrderProduct product : productList) {
			Product purchasedProduct = findByProducts(product.productId());
			orderProducts.add(OrderProduct.create(order, purchasedProduct, product.quantity()));
		}
		return orderProducts;
	}


	private Product findByProducts(UUID productId){
		return productJpaRepository.findById(productId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_PRODUCT.getMessage()));
	}

}
