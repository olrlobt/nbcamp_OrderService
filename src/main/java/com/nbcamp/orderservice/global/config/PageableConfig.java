package com.nbcamp.orderservice.global.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nbcamp.orderservice.domain.common.CustomPageableArgumentResolver;

@Configuration
public class PageableConfig implements WebMvcConfigurer {

	@Override
	public  void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers){
		resolvers.add(new CustomPageableArgumentResolver());
	}
}
