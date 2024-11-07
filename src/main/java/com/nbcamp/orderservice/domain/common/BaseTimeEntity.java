package com.nbcamp.orderservice.domain.common;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

	@CreatedDate
	@Column(name = "created_at", updatable = false, columnDefinition = "timestamp comment '생성일'")
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at", columnDefinition = "timestamp comment '수정일'")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at", columnDefinition = "timestamp comment '삭제일'")
	private LocalDateTime deletedAt;

	@CreatedBy
	@Column(name = "created_by", updatable = false, columnDefinition = "uuid comment '생성자'")
	private UUID createdBy;

	@LastModifiedBy
	@Column(name = "updated_by", columnDefinition = "uuid comment '수정자'")
	private UUID updatedBy;

	@Column(name = "deleted_by", columnDefinition = "uuid comment '삭제자'")
	private UUID deletedBy;

}
