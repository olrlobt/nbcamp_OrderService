package com.nbcamp.orderservice.domain.category.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping("/category")
	public ResponseEntity<CommonResponse<CategoryResponse>> createCategory(
		@RequestBody CategoryRequest categoryRequest
	){
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT,categoryService.createCategory(categoryRequest));
	}

	@GetMapping("/category/{categoryId}")
	public ResponseEntity<CommonResponse<CategoryResponse>> getCategory(@PathVariable String categoryId){
		return CommonResponse.success(SuccessCode.SUCCESS,categoryService.getCategory(categoryId));
	}

	@GetMapping("/category")
	public ResponseEntity<CommonResponse<List<CategoryResponse>>> getAllCategory(){
		return CommonResponse.success(SuccessCode.SUCCESS,categoryService.getAllCategory());
	}

	@PutMapping("/category/{categoryId}")
	public ResponseEntity<CommonResponse<CategoryResponse>> updateCategory(
		@PathVariable String categoryId,
		@RequestBody CategoryRequest categoryRequest){
		return CommonResponse
			.success(SuccessCode.SUCCESS_UPDATE, categoryService.updateCategory(categoryId, categoryRequest));
	}

	@DeleteMapping("/category/{categoryId}")
	public ResponseEntity<CommonResponse<Void>> delete(@PathVariable String categoryId){
		categoryService.deleteCategory(categoryId);
		return CommonResponse.success(SuccessCode.SUCCESS_UPDATE);
	}


}
