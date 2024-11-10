package com.nbcamp.orderservice.domain.store.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@PostMapping("/store/user/{userId}")
	public ResponseEntity<CommonResponse<StoreResponse>> createStore(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable String userId,
		@RequestBody StoreRequest storeRequest
	) {
		return CommonResponse.success(
			SuccessCode.SUCCESS_INSERT, storeService.createStore(userId, storeRequest, userDetails.getUser()));
	}

}
