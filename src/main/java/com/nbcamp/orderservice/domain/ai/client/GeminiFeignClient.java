package com.nbcamp.orderservice.domain.ai.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.nbcamp.orderservice.domain.ai.dto.ChatRequest;
import com.nbcamp.orderservice.domain.ai.dto.ChatResponse;

@FeignClient(value = "geminiClient", url = "${gemini.api.url}")
public interface GeminiFeignClient {

	@PostMapping
	ChatResponse createChat(@RequestParam("key") String apiKey, @RequestBody ChatRequest chatRequest);

}
