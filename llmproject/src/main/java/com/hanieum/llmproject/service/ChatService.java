package com.hanieum.llmproject.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.hanieum.llmproject.model.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanieum.llmproject.config.ChatGptConfig;
import com.hanieum.llmproject.dto.chat.ChatMessage;
import com.hanieum.llmproject.dto.chat.ChatRequestDto;
import com.hanieum.llmproject.dto.chat.ChatStreamResponseDto;
import com.hanieum.llmproject.model.Chat;
import com.hanieum.llmproject.model.Chatroom;
import com.hanieum.llmproject.repository.ChatRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

	private final ChatRepository chatRepository;
	private final ChatroomService chatroomService;
	private final DallEService dallEService;
	private final UserService userService;
	@Value("#{'${openai.key}'.trim()}")
	private String token;

	// 채팅내역 자동저장기능 (사용자답변, gpt답변분리)
	private void saveChat(Long chatroomId, boolean isUserMessage, boolean isImage, String message) {

		Chatroom chatroom = chatroomService.findChatroom(chatroomId);
		if (chatroom == null) {
			throw new IllegalArgumentException("채팅방을 찾을수없음.");
		}

		Chat chat = new Chat(chatroom, isUserMessage, isImage, message);

		chatRepository.save(chat);
	}

	// 이전답변+새답변 gpt에 재입력 (과거 대화 기억해서 답변시키기)
	private List<ChatMessage> compositeMessage(Long chatroomId, String question) {

		List<ChatMessage> messages = new ArrayList<>();

		// 이전메세지 가져오기
		Chatroom chatroom = chatroomService.findChatroom(chatroomId);
		if (chatroom == null) {
			throw new NullPointerException("채팅방을 찾을수없음.");
		}

		List<Chat> chatHistory = chatRepository.findAllByChatroom(chatroom);

		for (Chat chat : chatHistory) {
			messages.add(new ChatMessage(chat.isUserMessage() ? "user" : "assistant", chat.getMessage()));
			System.out.println("이전채팅 = " + chat.getMessage());
		}

		// 새 메세지 추가
		messages.add(new ChatMessage("user", question));
		return messages;
	}

	// 프롬프트엔지니어링 부분
	private List<ChatMessage> applyPromptEngineering(List<ChatMessage> messages, String categoryType) {
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

		// 필요 시 사용자 질문 수정
		// ChatMessage userMessage = messages.get(messages.size() - 1);
		// userMessage.setMessage("필요한 정보를 추가로 제시: " + userMessage.getMessage());

		return messages;
	}

	// 채팅방 제목설정
	private String createTitle(String question) {
		// 문장이 20보다 작은경우 처리
		int endIndex = Math.min(question.length(), 10);
		// 0 ~ 20 문자열 추출
		String title = question.substring(0, endIndex);
		System.out.println("substring = " + title);
		return title;
	}

	public List<String> getChats(Long chatroomId) {
		var chatroom = chatroomService.findChatroom(chatroomId);

		return chatRepository.findAllByChatroom(chatroom)
			.stream()
			.sorted(Comparator.comparing(Chat::getOutputTime))
			.map(Chat::getMessage)
			.toList();
	}

	// sse응답기능
	public SseEmitter ask(String loginId, Long chatroomId, String categoryType, String question) {

		// 질문 기반 채팅방제목생성
		String title = createTitle(question);

		// 채팅방 찾기(없으면 생성)
		Long chatRoomId = chatroomService.findOrCreateChatroom(loginId, chatroomId, categoryType, title);

		// gpt요청데이터 셋팅
		ChatRequestDto chatRequestDto = ChatRequestDto.builder().
			model(ChatGptConfig.CHAT_MODEL).
			maxTokens(ChatGptConfig.MAX_TOKEN).
			temperature(ChatGptConfig.TEMPERATURE).
			stream(true).
			messages(applyPromptEngineering(compositeMessage(chatRoomId, question), categoryType)).
			build();

		// 기타셋팅
		StringBuffer sb = new StringBuffer(); // 텍스트한번에 저장할 버퍼
		SseEmitter emitter = new SseEmitter((long)(5 * 60 * 1000)); // SseEmitter(기본시간)
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
						// 저장
						emitter.send("chat_room_id: " + chatRoomId);
						saveChat(chatRoomId, true, false, question);      // 사용자질문저장
						saveChat(chatRoomId, false, false, sb.toString()); // gpt답변저장
						emitter.complete();

						// 결과 확인용
						System.out.println("sb = " + sb);
					} else {    // 응답 진행중
						ObjectMapper mapper = new ObjectMapper().configure(
							DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
						ChatStreamResponseDto streamDto = mapper.readValue(line, ChatStreamResponseDto.class);
						ChatStreamResponseDto.Choice.Delta delta = streamDto.getChoices().get(0).getDelta();

						if (delta != null && delta.getContent() != null) {
							sb.append(delta.getContent());      // 버퍼에 sse답변모으기
							//emitter.send(delta.getContent());   // 프론트로 text만 출력시킴
							emitter.send("{\"content\": \"" + delta.getContent() + "\"}"); // json형식으로 전송
							//System.out.println("Sending to frontend: " + delta.getContent());
						}
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			})
			.doOnError(emitter::completeWithError)
			.doOnComplete(emitter::complete)
			.subscribe();

		return emitter;
	}

	public String askImage(Long chatroomId, String question) {
		String image = dallEService.generateImage(question);

		var title = createTitle(question);
		var userId = userService.getLoginId();

		var returnedChatroomId = chatroomService.findOrCreateChatroom(userId, chatroomId, "DESIGN", title);

		saveChat(returnedChatroomId, true, false, question);
		saveChat(returnedChatroomId, false, true, image);

		return image;
	}
}
