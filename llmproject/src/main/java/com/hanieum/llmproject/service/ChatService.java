package com.hanieum.llmproject.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.hanieum.llmproject.config.aiconfig.ChatGptConfig;
import com.hanieum.llmproject.dto.chat.ChatMessage;
import com.hanieum.llmproject.dto.chat.ChatRequestDto;
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
	private final GPTService gptService;

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
	public String ask(String loginId, Long chatroomId, String categoryType, String question) {

		// 질문 기반 채팅방제목생성
		String title = createTitle(question);

		// 채팅방 찾기(없으면 생성)
		Long chatRoomId = chatroomService.findOrCreateChatroom(loginId, chatroomId, categoryType, title);

		// gpt요청데이터 셋팅
		ChatRequestDto chatRequestDto = ChatRequestDto.builder().
			model(ChatGptConfig.CHAT_MODEL).
			maxTokens(ChatGptConfig.MAX_TOKEN).
			temperature(ChatGptConfig.TEMPERATURE).
			messages(gptService.applyPromptEngineering(compositeMessage(chatRoomId, question), categoryType)).
			build();

		String response = gptService.gptRequest(chatRequestDto);

		saveChat(chatroomId, true, false, question);
		saveChat(chatroomId, false, false, response);
		System.out.println(response);
		return response;
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
