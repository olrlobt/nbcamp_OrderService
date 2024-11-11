package com.nbcamp.orderservice.domain.category.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.nbcamp.orderservice.domain.category.dto.CategoryRequest;
import com.nbcamp.orderservice.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
	name = "p_category"
)
public class Category extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id;

	@Column(name = "category", nullable = false)
	private String category;

	public static Category create(CategoryRequest categoryRequest) {
		return Category.builder()
			.category(categoryRequest.category())
			.build();
	}

	public void update(CategoryRequest categoryRequest) {
		this.category = categoryRequest.category();
	}

	public void delete(UUID uuid) {
		this.setDeletedBy(uuid);
		this.setDeletedAt(LocalDateTime.now());
	}

}
