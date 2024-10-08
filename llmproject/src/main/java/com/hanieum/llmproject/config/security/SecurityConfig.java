package com.hanieum.llmproject.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hanieum.llmproject.config.cors.CorsConfig;
import com.hanieum.llmproject.config.jwt.ExceptionHandlerFilter;
import com.hanieum.llmproject.config.jwt.JwtFilter;
import com.hanieum.llmproject.config.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final CorsConfig corsConfig;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers("/users/login")
			.requestMatchers("/users/signup")
			.requestMatchers("/users/reissue");
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfig.corsConfiguration()))
			.csrf(AbstractHttpConfigurer::disable)

			.httpBasic(HttpBasicConfigurer::disable)

			.sessionManagement((sessionManagement) ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			.formLogin(AbstractHttpConfigurer::disable)

			.authorizeHttpRequests((authorizeHttpRequest) ->
				authorizeHttpRequest
					.requestMatchers("/users/login").permitAll()
					.requestMatchers("/users/signup").permitAll()
					.anyRequest().authenticated())

			.addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new ExceptionHandlerFilter(), JwtFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}
