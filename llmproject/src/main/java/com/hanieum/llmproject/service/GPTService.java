package com.hanieum.llmproject.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.hanieum.llmproject.dto.chat.ChatMessage;
import com.hanieum.llmproject.dto.chat.ChatRequestDto;
import com.hanieum.llmproject.dto.chat.ChatStreamResponseDto;
import com.hanieum.llmproject.model.Category;
import com.hanieum.llmproject.model.Chat;
import com.hanieum.llmproject.model.Chatroom;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class GPTService {
	@Value("#{'${openai.key}'.trim()}")
	private String token;

	private final ChatroomService chatroomService;

	// 프롬프트엔지니어링 부분
	public List<ChatMessage> applyPromptEngineering(List<ChatMessage> messages, String categoryType) {
		// 카테고리별로 프롬프트 엔지니어링 하기
		Category category = chatroomService.loadCategory(categoryType);

		// 공통 시스템 메세지
		messages.add(0,new ChatMessage("system", "너는 llm 코딩 자동화 어시스턴트 도구야. 개발자에게 친절하게 도움을 줘야해."));

		if (category == Category.PLAN) { // 계획단계에 맞는 적합한 답변을 지시
			// 개별 시스템 메시지 추가
			messages.add(1, new ChatMessage("system", "필요한기능명세서 목록을 출력해주는 역할을 해야해. 사용자가 주제나 구현하고싶은 기능에대해 얘기하면 그 기능을 구현하기위해 필요한 기능들을 상세하게 목록으로 만들어서 출력해줘."));
			messages.add(2, new ChatMessage("system", "한눈에 보기쉽게 정갈하게 정렬하거나 표로 표시해야해."));
			ChatMessage userMessage = messages.get(messages.size() - 1);
			userMessage.setContent("다음에 작성된 내용에 대해 기능명세서를 작성해줘 : " + userMessage.getContent());

		} else if (category == Category.CODE) { // 코딩단계에 맞는 적합한 답변을 지시
			// 개별 시스템 메시지 추가
			messages.add(1, new ChatMessage("system", "사용자의 코드나, 요구사항을 보고 원하는 기능을 구현하기위해 필요한 코드를 작성해주면돼."));
			messages.add(2, new ChatMessage("system", "최대한 최적화된 코드를 작성해야하고, 답변은 최대한 코드로, 필요시 주석으로 설명을 달아서 답변해."));

		} else if (category == Category.TEST) { // 테스트 단게에 맞는 적합한 답변을 지시
			// 개별 시스템 메시지 추가
			messages.add(1, new ChatMessage("system", "사용자가 보낸 코드나 요구사항을 보고 그에 적합한 테스트 코드를 작성해주거나, 테스트케이스를 제시해줘."));
			messages.add(2, new ChatMessage("system", "오류를 찾아내야하는 부분이므로 최대한 코드에대한 결함이나 최적화부분을 검증해줘."));

		} else if (category == Category.DEPLOY) { // 배포단계에 맞는 적합한 답변을 지시
			// 개별 시스템 메시지 추가
			messages.add(1, new ChatMessage("system", "사용자가 완성한 프로그램에대한 정보를 통해 그에대한 배포솔루션을 알려줘"));
			messages.add(2, new ChatMessage("system", "버전관리, cicd구축등에대한 조언을 작성해줘."));

		}

		System.out.println("messages = " + messages);
		return messages;
	}

	public void gptRequest(ChatRequestDto chatRequestDto, Consumer<String> onDate, Runnable onComplete) {

		WebClient client = WebClient.create("https://api.openai.com/v1");

		// gpt요청
		client.post().uri("/chat/completions")
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.body(BodyInserters.fromValue(chatRequestDto))
				.exchangeToFlux(response -> response.bodyToFlux(String.class))
				.doOnNext(line -> {
					try {
						if (line.equals("[DONE]")) {    // 응답 종료시
							onComplete.run();
						} else {    // 응답 진행중
							ObjectMapper mapper = new ObjectMapper().configure(
									DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
							ChatStreamResponseDto streamDto = mapper.readValue(line, ChatStreamResponseDto.class);
							ChatStreamResponseDto.Choice.Delta delta = streamDto.getChoices().get(0).getDelta();

							if (delta != null && delta.getContent() != null) {
								onDate.accept(delta.getContent());
							}
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				})
				.subscribe();

	}
}
