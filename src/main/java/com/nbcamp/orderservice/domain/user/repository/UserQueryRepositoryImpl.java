package com.nbcamp.orderservice.domain.user.repository;

import static com.nbcamp.orderservice.domain.user.entity.QUser.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.nbcamp.orderservice.domain.user.dto.AllUserResponse;
import com.nbcamp.orderservice.domain.user.dto.UserResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<UserResponse> findUserResponseByUserId(UUID userId) {
		return Optional.ofNullable(jpaQueryFactory.select(
				Projections.constructor(
					UserResponse.class,
					user.username
				)
			)
			.from(user)
			.where(user.deletedBy.isNull())
			.fetchOne());
	}

	@Override
	public AllUserResponse findAllUserResponse() {
		List<UserResponse> userResponses = jpaQueryFactory
			.select(
				Projections.constructor(
					UserResponse.class,
					user.username,
					user.userRole
				)
			)
			.from(user)
			.fetch();

		return new AllUserResponse(userResponses);
	}
}
