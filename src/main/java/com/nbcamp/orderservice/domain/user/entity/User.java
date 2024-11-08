package com.nbcamp.orderservice.domain.user.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.nbcamp.orderservice.domain.common.BaseTimeEntity;
import com.nbcamp.orderservice.domain.common.UserRole;
import com.nbcamp.orderservice.domain.user.dto.SignupRequest;
import com.nbcamp.orderservice.domain.user.dto.UserUpdateRequest;

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
	name = "p_user"
)
public class User extends BaseTimeEntity {

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

	@Column(length = 1000)
	private String refreshToken;

	public static User create(SignupRequest request, PasswordEncoder passwordEncoder) {
		return User.builder()
			.id(UUID.randomUUID())
			.username(request.username())
			.password(passwordEncoder.encode(request.password()))
			.userRole(request.userRole())
			.build();
	}

	public void update(UserUpdateRequest request){
		//todo. 사람 추가
		//todo. 데이터 추가
	}

	public void delete() {
		//todo. 사람 추가
		this.setDeletedAt(LocalDateTime.now());
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void destroyRefreshToken() {
		this.refreshToken = null;
	}
}
