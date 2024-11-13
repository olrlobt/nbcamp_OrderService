package com.nbcamp.orderservice.domain.user.api;

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
import org.springframework.web.bind.annotation.RestController;

import com.nbcamp.orderservice.domain.user.dto.AllUserResponse;
import com.nbcamp.orderservice.domain.user.dto.SignupRequest;
import com.nbcamp.orderservice.domain.user.dto.UserResponse;
import com.nbcamp.orderservice.domain.user.dto.UserUpdateRequest;
import com.nbcamp.orderservice.domain.user.service.UserService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/users/signup")
	public ResponseEntity<CommonResponse<UserResponse>> signup(@Valid @RequestBody SignupRequest signupRequest) {
		UserResponse signup = userService.signup(signupRequest);
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT, signup);
	}

	@PostMapping("/users/logout")
	public ResponseEntity<CommonResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
		userService.logout(userDetails.getUsername());
		return CommonResponse.success(SuccessCode.SUCCESS);
	}

	@GetMapping("/users/{userId}")
	public ResponseEntity<CommonResponse<UserResponse>> getUserDetail(@PathVariable String userId){
		UserResponse userDetail = userService.getUserDetail(userId);
		return CommonResponse.success(SuccessCode.SUCCESS, userDetail);
	}

	@PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
	@GetMapping("/users")
	public ResponseEntity<CommonResponse<AllUserResponse>> getAllUsers(){
		AllUserResponse allUsers = userService.getAllUsers();
		return CommonResponse.success(SuccessCode.SUCCESS, allUsers);
	}

	@PutMapping("/users/{userId}")
	public ResponseEntity<CommonResponse<UserResponse>> updateUser(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable String userId,
		@RequestBody UserUpdateRequest request) {
		UserResponse userResponse = userService.updateUser(userDetails, userId, request);
		return CommonResponse.success(SuccessCode.SUCCESS_UPDATE, userResponse);
	}

	@DeleteMapping("/users/{userId}")
	public ResponseEntity<CommonResponse<Void>> deleteUser(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable String userId){
		userService.deleteUser(userDetails, userId);
		return CommonResponse.success(SuccessCode.SUCCESS_DELETE);
	}
}
