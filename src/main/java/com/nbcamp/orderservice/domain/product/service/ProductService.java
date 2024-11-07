package com.nbcamp.orderservice.domain.product.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nbcamp.orderservice.domain.product.dto.CreateProductRequest;
import com.nbcamp.orderservice.domain.product.dto.ProductResponse;
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

	public ProductResponse createProduct(String storeId, CreateProductRequest request) {
		Store store = getStoreById(storeId);
		Product product = Product.create(request, store);
		productJpaRepository.save(product);

		return new ProductResponse(
			product.getId(),
			store.getId(),
			product.getName(),
			product.getDescription(),
			product.getPrice(),
			product.getDisplayStatus()
		);
	}

	public ProductResponse getProduct(String storeId, String productId) {
		Store store = getStoreById(storeId);
		Product product = productJpaRepository.findById(UUID.fromString(productId))
			.orElseThrow(() -> new IllegalArgumentException(String.valueOf(ErrorCode.NOT_FOUND_PRODUCT)));

		return new ProductResponse(
			product.getId(),
			store.getId(),
			product.getName(),
			product.getDescription(),
			product.getPrice(),
			product.getDisplayStatus()
		);
	}

	private Store getStoreById(String storeId) {
		return storeJpaRepository.findById(UUID.fromString(storeId))
			.orElseThrow(() -> new IllegalArgumentException(String.valueOf(ErrorCode.NOT_FOUND_STORE)));
	}

}
