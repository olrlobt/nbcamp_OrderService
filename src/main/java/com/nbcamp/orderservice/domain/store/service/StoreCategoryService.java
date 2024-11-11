package com.nbcamp.orderservice.domain.store.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.category.entity.Category;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.store.entity.StoreCategory;
import com.nbcamp.orderservice.domain.store.repository.StoreCategoryJpaRepository;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreCategoryService {

	private final StoreCategoryJpaRepository storeCategoryJpaRepository;

	@Transactional
	public List<StoreCategory> storeCategoryCreate(Store store, List<Category> categoryList){
		List<StoreCategory> storeCategories = new ArrayList<>();
		for (Category category: categoryList) {
			storeCategories.add(StoreCategory.create(store,category));
		}
		return storeCategories;
	}
	
	@Transactional
	public List<StoreCategory> storeCategoryUpdate(Store store, List<Category> categoryList){
		List<StoreCategory> categories = findAllByStoreCategory(String.valueOf(store.getId()));

		categories.removeIf(storeCategory ->
			categoryList.stream().noneMatch(category -> category.getId().equals(storeCategory.getCategory().getId()))
		);
		for (Category category : categoryList) {
			boolean exists = categories.stream()
				.anyMatch(storeCategory -> storeCategory.getCategory().getId().equals(category.getId()));

			if (!exists) {
				categories.add(StoreCategory.create(store, category));
			}
		}
		return categories;
	} 



	
	public List<StoreCategory> findAllByStoreCategory(String storeId){
		return storeCategoryJpaRepository.findAllByStoreId(UUID.fromString(storeId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_STORE.getMessage()));
	}
	

}
