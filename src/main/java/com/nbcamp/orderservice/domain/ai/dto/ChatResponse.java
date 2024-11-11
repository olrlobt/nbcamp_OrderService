package com.nbcamp.orderservice.domain.ai.dto;

import java.util.List;

public record ChatResponse(
	List<Candidate> candidates,
	PromptFeedback promptFeedback
) {

	public record Candidate(
		Content content,
		String finishReason,
		int index,
		List<SafetyRating> safetyRatings
	) {
	}

	public record Content(
		List<Parts> parts,
		String role
	) {
	}

	public record Parts(
		String text
	) {
	}

	public record SafetyRating(
		String category,
		String probability
	) {
	}

	public record PromptFeedback(
		List<SafetyRating> safetyRatings
	) {
	}
}
