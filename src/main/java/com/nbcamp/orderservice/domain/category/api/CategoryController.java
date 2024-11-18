package com.nbcamp.orderservice.domain.category.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.category.dto.CategoryRequest;
import com.nbcamp.orderservice.domain.category.dto.CategoryResponse;
import com.nbcamp.orderservice.domain.category.service.CategoryService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "카테고리 관련 API")
public class CategoryController {

	private final CategoryService categoryService;

	@Operation(summary = "카테고리 생성")
	@PreAuthorize("hasAnyRole('MASTER')")
	@PostMapping("/category")
	public ResponseEntity<CommonResponse<CategoryResponse>> createCategory(
		@RequestBody CategoryRequest categoryRequest
	) {
		return CommonResponse.success(
			SuccessCode.SUCCESS_INSERT, categoryService
				.createCategory(categoryRequest));
	}

	@Operation(summary = "카테고리 상세 조회")
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<CommonResponse<CategoryResponse>> getCategory(
		@PathVariable UUID categoryId) {
		return CommonResponse.success(SuccessCode.SUCCESS, categoryService.getCategory(categoryId));
	}

	@Operation(summary = "카테고리 목록 조회")
	@GetMapping("/category")
	public ResponseEntity<CommonResponse<List<CategoryResponse>>> getAllCategory(
	) {
		return CommonResponse.success(SuccessCode.SUCCESS, categoryService.getAllCategory());
	}

	@Operation(summary = "카테고리 수정")
	@PreAuthorize("hasAnyRole('MASTER')")
	@PutMapping("/category/{categoryId}")
	public ResponseEntity<CommonResponse<CategoryResponse>> updateCategory(
		@PathVariable UUID categoryId,
		@RequestBody CategoryRequest categoryRequest) {
		return CommonResponse
			.success(SuccessCode.SUCCESS_UPDATE, categoryService
				.updateCategory(categoryId, categoryRequest));
	}

	@Operation(summary = "카테고리 삭제")
	@PreAuthorize("hasAnyRole('MASTER')")
	@DeleteMapping("/category/{categoryId}")
	public ResponseEntity<CommonResponse<Void>> deleteCategory
		(
			@AuthenticationPrincipal UserDetailsImpl userDetails,
			@PathVariable UUID categoryId
		) {
		categoryService.deleteCategory(categoryId, userDetails.getUser());
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}

}
