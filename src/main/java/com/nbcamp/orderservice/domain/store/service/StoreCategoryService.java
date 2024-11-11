package com.nbcamp.orderservice.domain.store.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.nbcamp.orderservice.domain.category.entity.Category;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.store.entity.StoreCategory;
import com.nbcamp.orderservice.domain.store.repository.StoreCategoryJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StoreCategoryService {

	private final StoreCategoryJpaRepository storeCategoryJpaRepository;

	public List<StoreCategory> storeCategoryCreate(Store store, List<Category> categoryList){
		List<StoreCategory> storeCategories = new ArrayList<>();
		for (Category category: categoryList) {
			storeCategories.add(StoreCategory.create(store,category));
		}
		return storeCategories;
	}




}
