package com.nbcamp.orderservice.domain.store.entity;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
import com.nbcamp.orderservice.domain.common.StoreCategory;
import com.nbcamp.orderservice.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(
	name = "p_store"
)
public class Store extends BaseTimeEntity {

	@Id
	@Column(name = "id", columnDefinition = "uuid comment '매장 고유 번호'")
	private UUID id = UUID.randomUUID();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, columnDefinition = "uuid comment '회원 고유 번호'")
	private User user;

	@Column(name = "name", nullable = false, columnDefinition = "varchar comment '매장이름'")
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "store_category", nullable = false, columnDefinition = "varchar comment '매장 카테고리'")
	private StoreCategory storeCategory;

	@Column(name = "area", nullable = false, columnDefinition = "varchar comment '지역'")
	private String area;

	@Column(name = "address", nullable = false, columnDefinition = "varchar comment '주소'")
	private String address;

	@Column(name = "call_number", nullable = false, columnDefinition = "varchar comment '전화번호'")
	private String callNumber;

	@Column(name = "store_grade", nullable = false, columnDefinition = "double precision comment '매장 평점'")
	private double storeGrade;

}

