package com.hanieum.llmproject.service;

import java.io.IOException;
import java.util.List;

import com.hanieum.llmproject.dto.chat.ChatMessage;
import com.hanieum.llmproject.dto.chat.ChatRequestDto;
import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import com.hanieum.llmproject.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
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
