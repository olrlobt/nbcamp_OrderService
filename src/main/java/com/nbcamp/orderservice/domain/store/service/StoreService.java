package com.nbcamp.orderservice.domain.store.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.category.entity.Category;
import com.nbcamp.orderservice.domain.category.service.CategoryService;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.store.dto.StoreRequest;
import com.nbcamp.orderservice.domain.store.dto.StoreResponse;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.store.repository.StoreJpaRepository;
import com.nbcamp.orderservice.domain.store.repository.StoreQueryRepository;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.domain.user.service.UserService;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreJpaRepository storeJpaRepository;
	private final StoreQueryRepository storeQueryRepository;
	private final UserService userService;
	private final CategoryService categoryService;


	@Transactional
	public StoreResponse createStore(String userId, StoreRequest request, User user){
		checkMasterUserRoll(user);
		User owner = userService.findById(userId);
		List<Category> categories = findCategoryList(request.category());
		Store store = Store.create(request, owner, categories);
		storeJpaRepository.save(store);
		return new StoreResponse(
			store.getId(),
			store.getUser().getId(),
			store.getUser().getUsername(),
			store.getName(),
			store.getArea(),
			store.getAddress(),
			store.getCategories(),
			store.getCallNumber());
	}



	private List<Category> findCategoryList(List<String> categoryList){
		return categoryService.findCategoriesByNames(categoryList);
	}

	private void checkMasterUserRoll(User user){
		if (user.getUserRole().equals(UserRole.CUSTOMER)
			|| user.getUserRole().equals(UserRole.OWNER)
			|| user.getUserRole().equals(UserRole.MANAGER)) {
			throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_PERMISSIONS.getMessage());
		}
	}

}
