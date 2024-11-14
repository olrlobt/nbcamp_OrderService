package com.nbcamp.orderservice.domain.ai.dto;

import java.util.ArrayList;
import java.util.List;

public record ChatRequest(
	List<Content> contents,
	GenerationConfig generationConfig
) {

	public record Content(Parts parts) {
	}

	public record Parts(String text) {
	}

	public record GenerationConfig(
		int candidate_count,
		int max_output_tokens,
		double temperature
	) {
	}

	public ChatRequest(String prompt) {
		this(
			new ArrayList<>(),
			new GenerationConfig(1, 1000, 0.7)
		);

		Content content = new Content(new Parts(prompt));
		this.contents.add(content);
	}
}