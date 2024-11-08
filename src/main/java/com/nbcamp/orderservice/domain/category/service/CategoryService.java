package com.nbcamp.orderservice.domain.category.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.category.dto.CategoryRequest;
import com.nbcamp.orderservice.domain.category.dto.CategoryResponse;
import com.nbcamp.orderservice.domain.category.entity.Category;
import com.nbcamp.orderservice.domain.category.repository.CategoryJpaRepository;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryJpaRepository categoryJpaRepository;

	@Transactional
	public CategoryResponse createCategory(CategoryRequest request){
		Category category = Category.create(request);
		categoryJpaRepository.save(category);
		return new CategoryResponse(category.getId(), category.getCategory());
	}

	@Transactional(readOnly = true)
	public CategoryResponse getCategory(String categoryId){
		Category category = findById(categoryId);
		return new CategoryResponse(category.getId(), category.getCategory());
	}

	@Transactional(readOnly = true)
	public List<CategoryResponse> getAllCategory(){
		List<Category> categories = categoryJpaRepository.findAll();
		return categories.stream()
			.map(category -> new CategoryResponse(category.getId(), category.getCategory()))
			.collect(Collectors.toList());
	}

	public Category findById(String uuid){
		return categoryJpaRepository.findById(UUID.fromString(uuid))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_CATEGORY.getMessage()));
	}


}

