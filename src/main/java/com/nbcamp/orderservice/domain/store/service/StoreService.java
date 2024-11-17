package com.nbcamp.orderservice.domain.store.service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.category.entity.Category;
import com.nbcamp.orderservice.domain.category.repository.CategoryQueryRepository;
import com.nbcamp.orderservice.domain.store.dto.StoreCursorRequest;
import com.nbcamp.orderservice.domain.store.dto.StoreCursorResponse;
import com.nbcamp.orderservice.domain.store.dto.StoreDetailsResponse;
import com.nbcamp.orderservice.domain.store.dto.StoreRequest;
import com.nbcamp.orderservice.domain.store.dto.StoreResponse;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.store.entity.StoreCategory;
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
	private final CategoryQueryRepository categoryQueryRepository;
	private final UserService userService;
	private final StoreCategoryService storeCategoryService;

	@Transactional
	public StoreResponse createStore(String userId, StoreRequest request) {
		User owner = userService.findById(userId);
		Store store = Store.create(request, owner);

		List<Category> categories = findCategoryList(request.category());
		List<StoreCategory> storeCategories = storeCategoryService.createStoreCategory(store, categories);
		store.addStoreCategory(storeCategories);

		return new StoreResponse(storeJpaRepository.save(store));
	}

	@Transactional(readOnly = true)
	public StoreDetailsResponse getDetailsStore(UUID storeId) {
		Store store = findById(storeId);
		return new StoreDetailsResponse(store);
	}

	@Transactional(readOnly = true)
	public Slice<StoreCursorResponse> getCursorStore(StoreCursorRequest request, Pageable pageable, User user) {
		return storeQueryRepository.findAllByStorePageable(
			request.storeId(),
			request.categoryId(),
			extractAddress(request.address()),
			request.sortOption(),
			pageable,
			user
		);
	}

	@Transactional
	public StoreResponse updateStore(UUID storesId, StoreRequest request) {
		Store store = findById(storesId);

		List<StoreCategory> storeCategories =
			storeCategoryService.updateStoreCategory(store, findCategoryList(request.category()));

		store.update(request, storeCategories);

		return new StoreResponse(store);
	}

	@Transactional
	public void deletedStore(UUID storesId) {
		Store store = findById(storesId);
		storeCategoryService.deleteStoreCategory(store);
		store.delete(store.getId());
	}

	private List<Category> findCategoryList(List<UUID> categoryList) {
		return categoryQueryRepository.findAllCategoryByCategoryId(categoryList)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_CATEGORY.getMessage()));
	}


	private Store findById(UUID storeId) {
		return storeJpaRepository.findById(storeId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_STORE.getMessage()));
	}

	private String extractAddress(String fullAddress) {
		String addressPattern = "([가-힣]+[특별시|광역시|도])\\s([가-힣]+구)";
		Pattern pattern = Pattern.compile(addressPattern);
		Matcher matcher = pattern.matcher(fullAddress);

		if (matcher.find()) {
			return matcher.group(1) + " " + matcher.group(2);
		}
		throw new IllegalArgumentException(ErrorCode.ADDRESS_PATTERN_MISMATCH.getMessage());
	}

}
