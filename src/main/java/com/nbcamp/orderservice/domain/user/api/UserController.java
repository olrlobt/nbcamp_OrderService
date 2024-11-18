package com.nbcamp.orderservice.domain.user.api;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.common.SortOption;
import com.nbcamp.orderservice.domain.user.dto.SignupRequest;
import com.nbcamp.orderservice.domain.user.dto.UserResponse;
import com.nbcamp.orderservice.domain.user.dto.UserUpdateRequest;
import com.nbcamp.orderservice.domain.user.service.UserService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "유저 관련 API")
public class UserController {

	private final UserService userService;

	@Operation(summary = "회원가입")
	@PostMapping("/users/signup")
	public ResponseEntity<CommonResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
		UserResponse signup = userService.signup(signupRequest);
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT, signup);
	}

	@Operation(summary = "로그아웃")
	@PostMapping("/users/logout")
	public ResponseEntity<CommonResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
		userService.logout(userDetails.getUsername());
		return CommonResponse.success(SuccessCode.SUCCESS);
	}

	@Operation(summary = "회원 정보 조회")
	@GetMapping("/users/{userId}")
	public ResponseEntity<CommonResponse<UserResponse>> getUserDetail(@PathVariable UUID userId){
		UserResponse userDetail = userService.getUserDetail(userId);
		return CommonResponse.success(SuccessCode.SUCCESS, userDetail);
	}

	@Operation(summary = "회원 목록 조회")
	@PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
	@GetMapping("/users")
	public ResponseEntity<CommonResponse<Page<UserResponse>>> getAllUsers(
		@RequestParam(value = "sortOption", required = false, defaultValue = "CREATED_AT_ASC") SortOption sortOption,
		Pageable pageable
	) {
		Page<UserResponse> allUsers = userService.getAllUsers(sortOption, pageable);
		return CommonResponse.success(SuccessCode.SUCCESS, allUsers);
	}

	@Operation(summary = "회원 정보 수정")
	@PutMapping("/users/{userId}")
	public ResponseEntity<CommonResponse<UserResponse>> updateUser(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID userId,
		@RequestBody UserUpdateRequest request) {
		UserResponse userResponse = userService.updateUser(userDetails, userId, request);
		return CommonResponse.success(SuccessCode.SUCCESS_UPDATE, userResponse);
	}

	@Operation(summary = "회원 정보 삭제")
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<CommonResponse<Void>> deleteUser(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable UUID userId){
		userService.deleteUser(userDetails, userId);
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}

	@Operation(summary = "회원 권한 수정")
	@PreAuthorize("hasAnyRole('MASTER')")
	@PutMapping("/users/{userId}/role")
	public ResponseEntity<CommonResponse<Void>> updateUserRole(@PathVariable UUID userId, @RequestBody String role){
		userService.updateUserRole(userId, role);
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}
}
