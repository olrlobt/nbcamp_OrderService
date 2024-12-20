package com.nbcamp.orderservice.domain.store.api;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "매장 관련 API")
public class StoreController {

	private final StoreService storeService;

	@Operation(summary = "매장 생성")
	@PreAuthorize("hasAnyRole('MASTER')")
	@PostMapping("/stores/users/{userId}")
	public ResponseEntity<CommonResponse<StoreResponse>> createStore(
		@PathVariable UUID userId,
		@Valid @RequestBody StoreRequest storeRequest
	) {
		return CommonResponse.success(
			SuccessCode.SUCCESS_INSERT, storeService.createStore(userId, storeRequest));
	}

	@Operation(summary = "매장 상세 조회")
	@GetMapping("/stores/{storeId}")
	public ResponseEntity<CommonResponse<StoreDetailsResponse>> getDetailsStore(@PathVariable UUID storeId) {
		return CommonResponse.success(SuccessCode.SUCCESS, storeService.getDetailsStore(storeId));
	}

	@Operation(summary = "매장 목록 조회")
	@GetMapping("/stores")
	public ResponseEntity<CommonResponse<Slice<StoreCursorResponse>>> getCursorStore(
		@Valid @ModelAttribute StoreCursorRequest storeCursorRequest,
		Pageable pageable
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			storeService.getCursorStore(storeCursorRequest, pageable));
	}

	@Operation(summary = "관리자 전용 매장 목록 조회")
	@PreAuthorize("hasAnyRole('MASTER')")
	@GetMapping("/stores/admin")
	public ResponseEntity<CommonResponse<Slice<StoreCursorResponse>>> getCursorStoreAdmin(
		@Valid @ModelAttribute StoreCursorRequest storeCursorRequest,
		Pageable pageable
	) {
		return CommonResponse.success(SuccessCode.SUCCESS,
			storeService.getCursorStoreAdmin(storeCursorRequest, pageable));
	}

	@Operation(summary = "매장 정보 수정")
	@PreAuthorize("hasAnyRole('MASTER')")
	@PutMapping("/stores/{storeId}")
	public ResponseEntity<CommonResponse<StoreResponse>> updateStore(
		@PathVariable UUID storeId,
		@Valid @RequestBody StoreRequest storeRequest
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_UPDATE,
			storeService.updateStore(storeId, storeRequest));
	}

	@Operation(summary = "매장 삭제")
	@PreAuthorize("hasAnyRole('MASTER')")
	@DeleteMapping("/stores/{storeId}")
	public ResponseEntity<CommonResponse<Void>> deleteStore(
		@PathVariable UUID storeId
	) {
		storeService.deletedStore(storeId);
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}

}
