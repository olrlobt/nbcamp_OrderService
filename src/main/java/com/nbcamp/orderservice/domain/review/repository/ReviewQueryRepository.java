package com.nbcamp.orderservice.domain.review.repository;

import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.review.entity.QReview;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	QReview qReview = QReview.review;


}
