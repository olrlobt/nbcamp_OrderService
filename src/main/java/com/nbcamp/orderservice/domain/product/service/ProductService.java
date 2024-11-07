package com.nbcamp.orderservice.domain.product.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nbcamp.orderservice.domain.product.dto.CreateProductRequest;
import com.nbcamp.orderservice.domain.product.dto.CreateProductResponse;
import com.nbcamp.orderservice.domain.product.entity.Product;
import com.nbcamp.orderservice.domain.product.repository.ProductJpaRepository;
import com.nbcamp.orderservice.domain.product.repository.ProductQueryRepository;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.store.repository.StoreJpaRepository;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductJpaRepository productJpaRepository;
	private final ProductQueryRepository productQueryRepository;
	private final StoreJpaRepository storeJpaRepository;

	public CreateProductResponse createProduct(String storeId, CreateProductRequest request) {
		Store store = storeJpaRepository.findById(UUID.fromString(storeId))
			.orElseThrow(() -> new IllegalArgumentException(String.valueOf(ErrorCode.NOT_FOUND_STORE)));

		Product product = Product.create(request, store);
		productJpaRepository.save(product);

		return new CreateProductResponse(
			product.getId(),
			store.getId(),
			product.getName(),
			product.getDescription(),
			product.getPrice(),
			product.getDisplayStatus()
		);
	}
}
