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

import com.nbcamp.orderservice.domain.user.dto.LoginRequest;
import com.nbcamp.orderservice.domain.user.dto.SignupRequest;
import com.nbcamp.orderservice.domain.user.service.UserService;
import com.nbcamp.orderservice.global.exception.code.SuccessCode;
import com.nbcamp.orderservice.global.response.CommonResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/users/signup")
	public ResponseEntity<CommonResponse<Object>> signup(@RequestBody SignupRequest signupRequest){
		userService.signup(signupRequest);
		return CommonResponse.success(SuccessCode.SUCCESS_INSERT);
	}

	@PostMapping("/users/login")
	public ResponseEntity<CommonResponse<String>> login(@RequestBody LoginRequest loginRequest){
		String accessToken = userService.login(loginRequest);
		return CommonResponse.success(SuccessCode.SUCCESS, accessToken);
	}

	@PostMapping("/users/logout")
	public ResponseEntity<CommonResponse<Object>> logout(@AuthenticationPrincipal UserDetails userDetails){
		userService.logout(userDetails.getUsername());
		return CommonResponse.success(SuccessCode.SUCCESS);
	}

	@GetMapping("/users/{userId}")
	public void getUser(@PathVariable String userId){
		//todo. 개인별 상세 조회

	}

	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	@GetMapping("/users")
	public void getAllUsers(){
		log.info("users");
		//todo. 조건 검색 기능, 필터 기능

	}

	@PutMapping("/users/{userId}")
	public void updateUser(@PathVariable String userId){

	}

	@DeleteMapping("/users/{userId}")
	public void deleteUser(@PathVariable String userId){

	}

}
