package com.hanieum.llmproject.config.jwt;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final RequestAttributeSecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = jwtUtil.extractToken(request);

		jwtUtil.validateAccessToken(token);

		Authentication authentication = jwtUtil.getAuthentication(token);

		//추가 (sse 응답 access denied문제)
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authentication);
		securityContextRepository.saveContext(context, request, response);

		filterChain.doFilter(request, response);
	}
}
