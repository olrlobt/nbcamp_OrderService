package com.nbcamp.orderservice.domain.product.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.product.dto.ProductRequest;
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

	@Transactional
	public ProductResponse createProduct(String storeId, ProductRequest request) {
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

	@Transactional(readOnly = true)
	public ProductResponse getProduct(String storeId, String productId) {
		Store store = getStoreById(storeId);
		Product product = getProductById(productId);

		return new ProductResponse(
			product.getId(),
			store.getId(),
			product.getName(),
			product.getDescription(),
			product.getPrice(),
			product.getDisplayStatus()
		);
	}

	@Transactional(readOnly = true)
	public Page<ProductResponse> getAllProduct(String storeId, int page, int size) {
		UUID storeUuid = getStoreById(storeId).getId();
		Pageable pageable = PageRequest.of(page, size);

		return productQueryRepository.findAllProductResponsesByStoreId(storeUuid, pageable);
	}

	@Transactional
	public ProductResponse updateProduct(String storeId, String productId, ProductRequest request) {
		Store store = getStoreById(storeId);
		Product product = getProductById(productId);
		product.update(request);

		return new ProductResponse(
			product.getId(),
			store.getId(),
			product.getName(),
			product.getDescription(),
			product.getPrice(),
			product.getDisplayStatus()
		);
	}

	private Product getProductById(String productId) {
		return productJpaRepository.findById(UUID.fromString(productId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_PRODUCT.getMessage()));
	}

	private Store getStoreById(String storeId) {
		return storeJpaRepository.findById(UUID.fromString(storeId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_STORE.getMessage()));
	}

}
