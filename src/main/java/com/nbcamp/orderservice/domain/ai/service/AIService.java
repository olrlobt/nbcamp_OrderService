package com.nbcamp.orderservice.domain.ai.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbcamp.orderservice.domain.ai.client.GeminiFeignClient;
import com.nbcamp.orderservice.domain.ai.dto.AIRequest;
import com.nbcamp.orderservice.domain.ai.dto.AIResponse;
import com.nbcamp.orderservice.domain.ai.dto.ChatRequest;
import com.nbcamp.orderservice.domain.ai.dto.ChatResponse;
import com.nbcamp.orderservice.domain.ai.entity.AIRequestLog;
import com.nbcamp.orderservice.domain.ai.repository.AIJpaRepository;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.store.entity.Store;
import com.nbcamp.orderservice.domain.store.repository.StoreJpaRepository;
import com.nbcamp.orderservice.domain.user.entity.User;
import com.nbcamp.orderservice.global.exception.code.ErrorCode;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AIService {

	private final AIJpaRepository aiJpaRepository;
	private final StoreJpaRepository storeJpaRepository;
	private final GeminiFeignClient geminiFeignClient;

	@Value("${gemini.api.key}")
	private String apiKey;

	// 초당 1회 요청을 허용하고, 5초마다 새로 토큰을 충전
	private final Bucket bucket = Bucket.builder()
		.addLimit(Bandwidth.classic(1, Refill.greedy(1, Duration.ofSeconds(5))))
		.build();

	private static final String STORE_NAME_PROMPT = "우리 매장의 이름은 ";
	private static final String PRODUCT_DESCRIPTION_PROMPT = " 입니다. 배달 서비스에 상품 등록을 하려고 하는데, 내가 다음 문장에 주는 정보들을 이용해서 상품 설명 작성을 도와줘.";
	private static final String MAX_LENGTH_HINT = "답변은 최대한 간결하게 50자 이하로 작성해줘.";

	@Transactional
	public AIResponse createProductDescription(String storeId, AIRequest request, User user) {
		// Bucket을 사용한 요청 제한 체크
		if (!bucket.tryConsume(1)) {
			throw new IllegalStateException(ErrorCode.RATE_LIMIT_EXCEEDED.getMessage());
		}

		validateOwner(user.getUserRole());
		Store store = storeJpaRepository.findById(UUID.fromString(storeId))
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_STORE.getMessage()));

		String requestText =
			STORE_NAME_PROMPT + store.getName() + PRODUCT_DESCRIPTION_PROMPT + request.text() + MAX_LENGTH_HINT;
		ChatResponse chatResponse = geminiFeignClient.createChat(apiKey, new ChatRequest(requestText));

		if (chatResponse == null)
			throw new IllegalStateException(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
		String message = chatResponse.candidates().get(0).content().parts().get(0).text();

		aiJpaRepository.save(AIRequestLog.create(store, request.text(), message, user.getId()));

		return new AIResponse(message);
	}

	private void validateOwner(UserRole role) {
		if (!role.equals(UserRole.MANAGER) && !role.equals(UserRole.MASTER) && !role.equals(UserRole.OWNER)) {
			throw new IllegalArgumentException(ErrorCode.INSUFFICIENT_PERMISSIONS.getMessage());
		}
	}
}
