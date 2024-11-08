package com.nbcamp.orderservice.domain.category.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.nbcamp.orderservice.domain.category.dto.CategoryRequest;
import com.nbcamp.orderservice.domain.category.dto.CategoryResponse;
import com.nbcamp.orderservice.domain.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	@Column(name = "id", columnDefinition = "uuid category '카테고리 고유 번호'")
	private UUID id = UUID.randomUUID();

	@Column(name = "category", nullable = false, columnDefinition = "varchar comment '카테고리'")
	private String category;

	public static Category create(CategoryRequest categoryRequest){
		return Category.builder()
			.category(categoryRequest.category())
			.build();
	}

}
