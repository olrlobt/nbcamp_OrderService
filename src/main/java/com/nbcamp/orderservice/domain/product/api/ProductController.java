package com.nbcamp.orderservice.domain.product.api;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.product.dto.ProductRequest;
import com.nbcamp.orderservice.domain.product.dto.ProductResponse;
import com.nbcamp.orderservice.domain.product.service.ProductService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = {"/v1/api"})
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PostMapping("/stores/{storeId}/products")
	public ResponseEntity<CommonResponse<ProductResponse>> createProduct(
		@PathVariable("storeId") String storeId,
		@RequestBody ProductRequest request
		// + 유저 인증
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT, productService.createProduct(storeId, request));
	}

	@GetMapping("/stores/{storeId}/products/{productId}")
	public ResponseEntity<CommonResponse<ProductResponse>> getProduct(
		@PathVariable("storeId") String storeId,
		@PathVariable("productId") String productId
		// + 유저 인증
	) {
		return CommonResponse.success(SuccessCode.SUCCESS, productService.getProduct(storeId, productId));
	}

	@GetMapping("/stores/{storeId}/products")
	public ResponseEntity<CommonResponse<Page<ProductResponse>>> getAllProduct(
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@PathVariable("storeId") String storeId
		// + 유저 인증
	) {
		return CommonResponse.success(SuccessCode.SUCCESS, productService.getAllProduct(storeId, page - 1, size));
	}

	@PutMapping("/stores/{storeId}/products/{productId}")
	public ResponseEntity<CommonResponse<ProductResponse>> updateProduct(
		@PathVariable("storeId") String storeId,
		@PathVariable("productId") String productId,
		@RequestBody ProductRequest request
		// + 유저 인증
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT,
			productService.updateProduct(storeId, productId, request));
	}

	@DeleteMapping("/stores/{storeId}/products/{productId}")
	public ResponseEntity<CommonResponse<Void>> deleteProduct(
		@PathVariable("storeId") String storeId,
		@PathVariable("productId") String productId
		// + 유저 인증
	) {
		productService.deleteProduct(storeId, productId);
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}

}