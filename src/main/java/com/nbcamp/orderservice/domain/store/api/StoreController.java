package com.nbcamp.orderservice.domain.store.api;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.store.dto.StoreCursorRequest;
import com.nbcamp.orderservice.domain.store.dto.StoreCursorResponse;
import com.nbcamp.orderservice.domain.store.dto.StoreDetailsResponse;
import com.nbcamp.orderservice.domain.store.dto.StoreRequest;
import com.nbcamp.orderservice.domain.store.dto.StoreResponse;
import com.nbcamp.orderservice.domain.store.service.StoreService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StoreController {

	private final StoreService storeService;

	@PreAuthorize("hasAnyRole('MASTER')")
	@PostMapping("/stores/users/{userId}")
	public ResponseEntity<CommonResponse<StoreResponse>> createStore(
		@PathVariable String userId,
		@Valid @RequestBody StoreRequest storeRequest
	) {
		return CommonResponse.success(
			SuccessCode.SUCCESS_INSERT, storeService.createStore(userId, storeRequest));
	}

	@GetMapping("/stores/{storeId}")
	public ResponseEntity<CommonResponse<StoreDetailsResponse>> getDetailsStore(@PathVariable UUID storeId) {
		return CommonResponse.success(SuccessCode.SUCCESS, storeService.getDetailsStore(storeId));
	}

	@GetMapping("/stores")
	public ResponseEntity<CommonResponse<Slice<StoreCursorResponse>>> getCursorStore(
		@Valid @ModelAttribute StoreCursorRequest storeCursorRequest,
		Pageable pageable,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			storeService.getCursorStore(storeCursorRequest, pageable, userDetails.getUser()));
	}

	@PreAuthorize("hasAnyRole('MASTER')")
	@PutMapping("/stores/{storeId}")
	public ResponseEntity<CommonResponse<StoreResponse>> updateStore(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable String storeId,
		@RequestBody StoreRequest storeRequest
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_UPDATE,
			storeService.updateStore(userDetails.getUser(), storeId, storeRequest));
	}

	@PreAuthorize("hasAnyRole('MASTER')")
	@DeleteMapping("/stores/{storeId}")
	public ResponseEntity<CommonResponse<Void>> deleteStore(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable String storeId
	){
		storeService.deletedStore(userDetails.getUser(), storeId);
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}

}
