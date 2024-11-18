package com.nbcamp.orderservice.domain.ai.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.ai.dto.AIRequest;
import com.nbcamp.orderservice.domain.ai.dto.AIResponse;
import com.nbcamp.orderservice.domain.ai.service.AIService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = {"/api/v1"})
@RequiredArgsConstructor
@Tag(name = "AI 관련 API")
public class AIController {

	private final AIService aiService;

	@Operation(summary = "상품 설명 등록 서포트 ai")
	@PostMapping("/stores/{storeId}/products/description/ai")
	public ResponseEntity<CommonResponse<AIResponse>> createProductDescription(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable("storeId") UUID storeId,
		@RequestBody AIRequest request
	) {
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT,
			aiService.createProductDescription(storeId, request, userDetails.getUser()));
	}

}
