package com.nbcamp.orderservice.domain.store.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.store.dto.StoreDetailsResponse;
import com.nbcamp.orderservice.domain.store.dto.StoreRequest;
import com.nbcamp.orderservice.domain.store.dto.StoreResponse;
import com.nbcamp.orderservice.domain.store.service.StoreService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class StoreController {

	private final StoreService storeService;

	@PostMapping("/stores/users/{usersId}")
	public ResponseEntity<CommonResponse<StoreResponse>> createStore(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable String usersId,
		@RequestBody StoreRequest storeRequest
	) {
		return CommonResponse.success(
			SuccessCode.SUCCESS_INSERT, storeService.createStore(usersId, storeRequest, userDetails.getUser()));
	}

	@GetMapping("/stores/{storeId}")
	public ResponseEntity<CommonResponse<StoreDetailsResponse>> getDetailsStore(@PathVariable String storeId) {
		return CommonResponse.success(SuccessCode.SUCCESS, storeService.getDetailsStore(storeId));
	}

}
