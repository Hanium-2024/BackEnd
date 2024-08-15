package com.hanieum.llmproject.config.aiconfig;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.theokanning.openai.service.OpenAiService;

@Configuration
public class ChatGptConfig {
	public static final String AUTHORIZATION = "Authorization";
	public static final String BEARER = "Bearer ";
	public static final String CHAT_MODEL = "gpt-4o-mini";
	public static final Integer MAX_TOKEN = 1000;
	public static final Boolean STREAM = true;
	public static final String ROLE = "user";
	public static final Double TEMPERATURE = 0.6;
	public static final String MEDIA_TYPE = "application/json; charset=UTF-8";
	public static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";

	@Value("#{'${openai.key}'.trim()}")
	private String token;

	@Bean
	public OpenAiService openAiService() {
		return new OpenAiService(token, Duration.ofSeconds(30L));
	}
}
