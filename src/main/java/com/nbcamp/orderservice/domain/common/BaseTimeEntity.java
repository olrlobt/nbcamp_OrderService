package com.nbcamp.orderservice.domain.common;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	@Comment("timestamp comment 생성일")
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	@Comment("timestamp comment 수정일")
	private LocalDateTime updatedAt;

	@Setter
	@Column(name = "deleted_at")
	@Comment("timestamp comment 삭제일")
	private LocalDateTime deletedAt;

	@CreatedBy
	@Column(name = "created_by", updatable = false)
	@Comment("uuid comment 생성자")
	private UUID createdBy;

	@LastModifiedBy
	@Column(name = "updated_by")
	@Comment("uuid comment 수정자")
	private UUID updatedBy;

	@Column(name = "deleted_by")
	@Comment("uuid comment 삭제자")
	private UUID deletedBy;

}
