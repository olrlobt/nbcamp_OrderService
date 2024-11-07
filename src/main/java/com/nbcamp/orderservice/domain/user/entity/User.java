package com.nbcamp.orderservice.domain.user.entity;

import java.util.UUID;

import com.nbcamp.orderservice.domain.common.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
	name = "user"
)
public class User {

	@Id
	@Column(name = "id", columnDefinition = "uuid comment '회원 고유 번호'")
	private UUID id = UUID.randomUUID();

	@Column(name = "username", nullable = false, columnDefinition = "varchar comment '회원 아이디'")
	private String username;

	@Column(name = "password", nullable = false, columnDefinition = "varchar comment '비밀번호'")
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_role", nullable = false, columnDefinition = "varchar comment '회원 권한'")
	private UserRole userRole;

}
