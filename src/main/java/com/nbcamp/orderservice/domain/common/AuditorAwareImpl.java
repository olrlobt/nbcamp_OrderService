package com.nbcamp.orderservice.domain.common;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.nbcamp.orderservice.global.exception.code.ErrorCode;
import com.nbcamp.orderservice.global.security.UserDetailsImpl;

@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

	@Override
	public Optional<UUID> getCurrentAuditor(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		checkAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
		return Optional.of(userDetails.getUser().getId());
	}

	private void checkAuthentication(Authentication authentication){
		if(authentication == null || !authentication.isAuthenticated()){
			throw new IllegalArgumentException(ErrorCode.NOT_FOUND_MEMBER.getMessage());
		}
	}

}
