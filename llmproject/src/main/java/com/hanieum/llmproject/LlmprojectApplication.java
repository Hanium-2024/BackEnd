package com.hanieum.llmproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// (exclude = ?) db 연결하지 않고 사용하기 위한 임시 어노테이션
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class LlmprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(LlmprojectApplication.class, args);
	}

}
