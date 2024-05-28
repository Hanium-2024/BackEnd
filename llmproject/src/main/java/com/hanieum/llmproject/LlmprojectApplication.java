package com.hanieum.llmproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)//스프링시큐리티방지
public class LlmprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(LlmprojectApplication.class, args);
	}

}
