package com.nbcamp.orderservice.domain.store.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
import com.nbcamp.orderservice.domain.store.dto.StoreRequest;
import com.nbcamp.orderservice.domain.user.entity.User;

import jakarta.persistence.CascadeType;
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
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "name", nullable = false)
	private String name;

	@OneToMany(mappedBy = "store",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<StoreCategory> storeCategory = new ArrayList<>();

	@Column(name = "address", nullable = false)
	private String address;

	@Column(name = "call_number", nullable = false)
	private String callNumber;

	@Column(name = "store_grade", nullable = false)
	private double storeGrade;

	@Column(name = "store_grade_reviews", nullable = false)
	private int storeGradeReviews;

	public static Store create(StoreRequest request, User owner){
		return Store.builder()
			.user(owner)
			.name(request.name())
			.address(request.address())
			.callNumber(request.callNumber())
			.storeGrade(0)
			.storeGradeReviews(0)
			.build();
	}

	public void update(StoreRequest storeRequest, List<StoreCategory> storeCategories){
		this.name = storeRequest.name();
		this.storeCategory = storeCategories;
		this.address = storeRequest.address();
		this.callNumber = storeRequest.callNumber();
	}

	public void delete(UUID storesId){
		this.setDeletedBy(storesId);
		this.setDeletedAt(LocalDateTime.now());
	}

	public void addStoreCategory(List<StoreCategory> storeCategories){
		this.storeCategory = storeCategories;
	}

	public void addStoreGrade(int grade){
		double totalGrade = this.storeGrade * this.storeGradeReviews + grade;
		this.storeGradeReviews += 1;
		this.storeGrade = totalGrade/ this.storeGradeReviews;
	}

	public void updateStoreGrade(int updatedGrade, int previousGrade){
		double totalGrade = this.storeGrade * this.storeGradeReviews;
		totalGrade = totalGrade - previousGrade + updatedGrade;
		this.storeGrade = totalGrade / this.storeGradeReviews;
	}

	public void removeStoreGrade(int removedGrade){
		if (this.storeGradeReviews <= 1) {

			this.storeGrade = 0;
			this.storeGradeReviews = 0;
		} else {
			double totalGrade = this.storeGrade * this.storeGradeReviews;
			totalGrade -= removedGrade;
			this.storeGradeReviews -= 1;
			this.storeGrade = totalGrade / this.storeGradeReviews;
		}
	}



}

