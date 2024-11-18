package com.nbcamp.orderservice.domain.product.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.product.dto.ProductRequest;
import com.nbcamp.orderservice.domain.product.dto.ProductResponse;
import com.nbcamp.orderservice.domain.product.entity.Product;
import com.nbcamp.orderservice.domain.product.repository.ProductJpaRepository;
import com.nbcamp.orderservice.domain.product.repository.ProductQueryRepository;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.store.repository.StoreJpaRepository;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductJpaRepository productJpaRepository;
	private final ProductQueryRepository productQueryRepository;
	private final StoreJpaRepository storeJpaRepository;

	@Transactional
	public ProductResponse createProduct(UUID storeId, ProductRequest request, User user) {
		validateOwner(user.getUserRole());
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
	public ProductResponse getProduct(UUID storeId, UUID productId, User user) {
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
	public Page<ProductResponse> getAllProducts(UUID storeId, int page, int size, SortOption sortOption, User user) {
		UUID storeUuid = getStoreById(storeId).getId();
		Pageable pageable = PageRequest.of(page, size);

		return productQueryRepository.findAllProductResponsesByStoreId(storeUuid, pageable, sortOption);
	}

	@Transactional(readOnly = true)
	public Page<ProductResponse> searchProducts(UUID storeId, int page, int size, String keyword,
		SortOption sortOption, User user) {
		UUID storeUuid = getStoreById(storeId).getId();
		Pageable pageable = PageRequest.of(page, size);

		return productQueryRepository.searchProducts(storeUuid, pageable, keyword, sortOption);
	}

	@Transactional
	public ProductResponse updateProduct(UUID storeId, UUID productId, ProductRequest request, User user) {
		validateOwner(user.getUserRole());
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

	@Transactional
	public void deleteProduct(UUID storeId, UUID productId, User user) {
		validateOwner(user.getUserRole());
		getStoreById(storeId);
		Product product = getProductById(productId);
		product.delete(user.getId());
	}

	private void validateOwner(UserRole role) {
		if (!role.equals(UserRole.MANAGER) && !role.equals(UserRole.MASTER) && !role.equals(UserRole.OWNER)) {
			throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_PERMISSIONS.getMessage());
		}
	}

	private Product getProductById(UUID productId) {
		return productJpaRepository.findById(productId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_PRODUCT.getMessage()));
	}

	private Store getStoreById(UUID storeId) {
		return storeJpaRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_STORE.getMessage()));
	}

}
