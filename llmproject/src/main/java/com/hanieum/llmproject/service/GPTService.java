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
import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
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
			// 공통프롬프트
			messages.add(1, new ChatMessage("system", "너는 소프트웨어 개발의 계획단계에 필요한 기능명세서를 출력해주는 모델이야."));
			messages.add(2, new ChatMessage("system", "소프트웨어 개발의 계획단계에 필요한 기능명세서 요청과 관련없는 질문에 대해서는 사용자에게 질문을 재입력하도록 요구해."));
			messages.add(3, new ChatMessage("system", "프로그램을 개발하기위해 필요한 기능명세서를 출력해주는거니까 표형태로 가시성있게 답변을 해줘야해."));
			messages.add(4, new ChatMessage("system", "요청받은것에 대해 최대한 상세하게 많은 기능을 명세해줘야해."));

			// 부가프롬프트
			messages.add(5, new ChatMessage("system", "기능 명세서를 작성하기에 부족한 정보를 제시한다면 너가 스스로 질문을 되물어서 자세한 정보를 받아내야해."));

			//ChatMessage userMessage = messages.get(messages.size() - 1);
			//userMessage.setContent("다음에 작성된 내용에 대해 기능명세서를 작성해줘 : " + userMessage.getContent());

		} else if (category == Category.CODE) { // 코딩단계에 맞는 적합한 답변을 지시
			// 공통 프롬프트 메시지
			messages.add(1, new ChatMessage("system", "코드를 보완해달라거나 작성해달라는 요청에는 무조건 코드로 답해주어야 해. 필요에 따라 추가로 서술할 수 있어."));
			messages.add(2, new ChatMessage("system", "최대한 최적화된 코드를 작성해야하고, 답변은 최대한 코드로, 필요시 주석으로 설명을 달아서 답변해."));
			messages.add(3, new ChatMessage("system", "사용자가 프로그래밍 언어를 명시하지 않았거나 프로그래밍 언어를 유추할 수 없다면 임의로 작성하지 말고 사용자에게 어떤 언어를 사용하여 코드를 작성해야하냐고 다시 물어서 정보를 얻어."));
			messages.add(4, new ChatMessage("system", "코드는 오류가 나지 않는, 안전하고 완전한 코드로 응답해주어야 해."));
			messages.add(5, new ChatMessage("system", "코드를 작성해줄 때, 들여쓰기는 \\t 를 사용해서 표시해줘."));

			// 부가 프롬프트 메시지
			messages.add(6, new ChatMessage("system", "코드 생성이나 리팩토링 등 그 외 코드와 관련되지 않은 요청에는 사용자에게 관련되지 않은 요청이라고 응답해주고 다시 코드와 관련된 요청을 달라고 응답해."));
			messages.add(7, new ChatMessage("system", "코드를 생성하는 요청에서 코드를 생성하기에 정보가 부족하거나 없다면 코드를 완성시키기 위해 필요한 정보를 사용자에게 요청해야해"));

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

	public String gptRequest(ChatRequestDto chatRequestDto) {

		WebClient client = WebClient.create("https://api.openai.com/v1");

		// gpt요청
		String response = client.post()
				.uri("/chat/completions")
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + token)
				.body(BodyInserters.fromValue(chatRequestDto))
				.retrieve()
				.bodyToMono(String.class)
				.block();

		// 응답에서 content 부분 추출
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response);
			JsonNode choicesNode = root.path("choices").get(0);
			String content = choicesNode.path("message").path("content").asText();

			return content;
		} catch (IOException e) {
			throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
		}
	}
}
