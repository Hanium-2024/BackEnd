package com.hanieum.llmproject.config.aiconfig;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.theokanning.openai.service.OpenAiService;

@Configuration
public class ChatGptConfig {
	@Value("#{'${openai.key}'.trim()}")
	private String token;

	@Bean
	public OpenAiService openAiService() {
		return new OpenAiService(token, Duration.ofSeconds(30L));
	}
}
