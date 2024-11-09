package com.nbcamp.orderservice.domain.store.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.nbcamp.orderservice.domain.category.entity.Category;
import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
import com.nbcamp.orderservice.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id")
	private UUID id = UUID.randomUUID();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "name", nullable = false)
	private String name;

	@OneToMany(fetch = FetchType.LAZY)
	private List<Category> categories = new ArrayList<>();

	@Column(name = "area", nullable = false)
	private String area;

	@Column(name = "address", nullable = false)
	private String address;

	@Column(name = "call_number", nullable = false)
	private String callNumber;

	@Column(name = "store_grade", nullable = false)
	private double storeGrade;

}

