package com.nbcamp.orderservice.domain.product.api;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.product.dto.ProductRequest;
import com.nbcamp.orderservice.domain.product.dto.ProductResponse;
import com.nbcamp.orderservice.domain.product.service.ProductService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = {"/api/v1"})
@RequiredArgsConstructor
@Tag(name = "상품 관련 API")
public class ProductController {

	private final ProductService productService;

	@Operation(summary = "상품 등록")
	@PostMapping("/stores/{storeId}/products")
	public ResponseEntity<CommonResponse<ProductResponse>> createProduct(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("storeId") UUID storeId,
		@RequestBody ProductRequest request
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT,
			productService.createProduct(storeId, request, userDetails.getUser()));
	}

	@Operation(summary = "상품 상세 조회")
	@GetMapping("/stores/{storeId}/products/{productId}")
	public ResponseEntity<CommonResponse<ProductResponse>> getProduct(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("storeId") UUID storeId,
		@PathVariable("productId") UUID productId
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			productService.getProduct(storeId, productId, userDetails.getUser()));
	}

	@Operation(summary = "상품 목록 조회")
	@GetMapping("/stores/{storeId}/products")
	public ResponseEntity<CommonResponse<Page<ProductResponse>>> getAllProducts(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@PathVariable("storeId") UUID storeId,
		@RequestParam(value = "sortOption", required = false, defaultValue = "CREATED_AT_ASC") SortOption sortOption
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			productService.getAllProducts(storeId, page - 1, size, sortOption, userDetails.getUser()));
	}

	@Operation(summary = "상품 검색")
	@GetMapping("/stores/{storeId}/products/search")
	public ResponseEntity<CommonResponse<Page<ProductResponse>>> searchProducts(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam("page") int page,
		@RequestParam("size") int size,
		@PathVariable("storeId") UUID storeId,
		@RequestParam(value = "keyword", required = false) String keyword,
		@RequestParam(value = "sortOption", required = false, defaultValue = "CREATED_AT_ASC") SortOption sortOption
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			productService.searchProducts(storeId, page - 1, size, keyword, sortOption, userDetails.getUser()));
	}

	@Operation(summary = "상품 수정")
	@PutMapping("/stores/{storeId}/products/{productId}")
	public ResponseEntity<CommonResponse<ProductResponse>> updateProduct(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("storeId") UUID storeId,
		@PathVariable("productId") UUID productId,
		@RequestBody ProductRequest request
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT,
			productService.updateProduct(storeId, productId, request, userDetails.getUser()));
	}

	@Operation(summary = "상품 삭제")
	@DeleteMapping("/stores/{storeId}/products/{productId}")
	public ResponseEntity<CommonResponse<Void>> deleteProduct(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("storeId") UUID storeId,
		@PathVariable("productId") UUID productId
	) {
		productService.deleteProduct(storeId, productId, userDetails.getUser());
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}

}
