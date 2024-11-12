package com.nbcamp.orderservice.domain.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.category.entity.Category;
import com.nbcamp.orderservice.domain.category.entity.QCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	QCategory qCategory = QCategory.category1;

	public Optional<List<Category>> findAllCategoryByCategoryId(List<String> categoryList){
		List<Category> categories = jpaQueryFactory
			.selectFrom(qCategory)
			.where(qCategory.category.in(categoryList)).fetch();

		if(categories.size() != categoryList.size()){
			return Optional.empty();
		}
		return Optional.of(categories);
	}
}
