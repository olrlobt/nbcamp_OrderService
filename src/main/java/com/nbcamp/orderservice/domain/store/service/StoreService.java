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
import com.nbcamp.orderservice.domain.category.service.CategoryService;
import com.nbcamp.orderservice.domain.common.UserRole;
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
	private final UserService userService;
	private final CategoryService categoryService;
	private final StoreCategoryService storeCategoryService;

	@Transactional
	public StoreResponse createStore(String userId, StoreRequest request, User user) {
		checkMasterUserRoll(user);
		validateAddressPattern(request.address());
		User owner = userService.findById(userId);
		Store store = Store.create(request, owner);

		List<Category> categories = findCategoryList(request.category());
		List<StoreCategory> storeCategories = storeCategoryService.createStoreCategory(store, categories);
		store.addStoreCategory(storeCategories);
		storeJpaRepository.save(store);

		return new StoreResponse(
			store.getId(),
			store.getUser().getId(),
			store.getUser().getUsername(),
			store.getName(),
			store.getAddress(),
			store.getStoreCategory()
				.stream()
				.map(storeCategory -> storeCategory.getCategory().getCategory())
				.toList(),
			store.getCallNumber());
	}

	@Transactional(readOnly = true)
	public StoreDetailsResponse getDetailsStore(String storesId) {
		Store store = findById(storesId);
		return new StoreDetailsResponse(
			store.getId(),
			store.getUser().getUsername(),
			store.getName(),
			store.getAddress(),
			store.getCallNumber()
		);
	}

	@Transactional(readOnly = true)
	public Slice<StoreCursorResponse> getCursorStore(String cursorId, String category, String address,
		Pageable pageable) {
		return storeQueryRepository.findAllByStorePageable(cursorId, category, extractAddress(address), pageable);
	}

	@Transactional
	public StoreResponse updateStore(User user, String storesId, StoreRequest request) {
		checkMasterUserRoll(user);
		validateAddressPattern(request.address());
		Store store = findById(storesId);

		List<StoreCategory> storeCategories =
			storeCategoryService.updateStoreCategory(store, findCategoryList(request.category()));

		store.update(request, storeCategories);

		return new StoreResponse(
			store.getId(),
			store.getUser().getId(),
			store.getUser().getUsername(),
			store.getName(),
			store.getAddress(),
			store.getStoreCategory()
				.stream()
				.map(storeCategory -> storeCategory.getCategory().getCategory())
				.toList(),
			store.getCallNumber());
	}

	@Transactional
	public void deletedStore(User user, String storesId){
		checkMasterUserRoll(user);
		Store store = findById(storesId);
		storeCategoryService.deleteStoreCategory(store);
		store.delete(store.getId());
	}


	private List<Category> findCategoryList(List<String> categoryList) {
		return categoryService.findCategoriesByNames(categoryList);
	}

	private void checkMasterUserRoll(User user) {
		if (user.getUserRole().equals(UserRole.CUSTOMER)
			|| user.getUserRole().equals(UserRole.OWNER)
			|| user.getUserRole().equals(UserRole.MANAGER)) {
			throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_PERMISSIONS.getMessage());
		}
	}

	private void validateAddressPattern(String fullAddress) {
		String addressPattern = "([가-힣]+[특별시|광역시|도])\\s([가-힣]+구)";
		Pattern pattern = Pattern.compile(addressPattern);
		Matcher matcher = pattern.matcher(fullAddress);

		if (matcher.find()) {
			return;
		}

		throw new IllegalArgumentException(ErrorCode.ADDRESS_PATTERN_MISMATCH.getMessage());
	}

	public Store findById(String uuid) {
		return storeJpaRepository.findById(UUID.fromString(uuid))
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
