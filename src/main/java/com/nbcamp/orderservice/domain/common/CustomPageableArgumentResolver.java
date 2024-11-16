package com.nbcamp.orderservice.domain.common;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CustomPageableArgumentResolver extends PageableHandlerMethodArgumentResolver  {

	@Override
	public Pageable resolveArgument(
		MethodParameter methodParameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory
	) {
		Pageable pageable = super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
		pageable = validatePageSize(pageable);


		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
	}

	private Pageable validatePageSize(Pageable pageable) {
		int pageSize = pageable.getPageSize();
		if (pageSize == 10 || pageSize == 30 || pageSize == 50) {
			return pageable;
		} else {
			return PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
		}
	}


}

