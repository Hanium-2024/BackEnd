package com.hanieum.llmproject.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import com.hanieum.llmproject.model.Category;
import net.sourceforge.plantuml.SourceStringReader;
import org.springframework.stereotype.Service;

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
			throw new CustomException(ErrorCode.CHATROOM_NOT_FOUND);
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
			throw new CustomException(ErrorCode.CHATROOM_NOT_FOUND);
		}

		List<Chat> chatHistory = chatRepository.findAllByChatroom(chatroom);

		for (Chat chat : chatHistory) {
			messages.add(new ChatMessage(chat.isUserMessage() ? "user" : "assistant", chat.getMessage()));
			//System.out.println("이전채팅 = " + chat.getMessage());
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
	public String ask(String loginId, Long chatroomId, String categoryType, String question) throws IOException {

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

		// gpt 응답
		String response = gptService.gptRequest(chatRequestDto);
		System.out.println("response = \n" + response);

		// 디자인일때
		Category category = chatroomService.loadCategory(categoryType);
		if (category== Category.DESIGN) {

			// 질문저장
			saveChat(chatroomId, true, false, question);

			// 응답형식 a1 , a2 분리
			String[] answers = response.split("A2:");

			// plant uml 코드부분 -> plant uml에 전달 -> 이미지 blob저장(Base64로 인코딩된 JSON 문자열을 저장)
			if (answers[0].replace("A1:", "").trim().equals("없음")) {
				System.out.println("Answer 1: 생성할 plant uml코드가 없습니다.");
				System.out.println("Answer 2: " + answers[1].replace("A2:", "").trim());
				saveChat(chatroomId, true, false, answers[1].replace("A2:", "").trim());
				//System.out.println("설계단계 전체출력: \n" + response);
			} else {
				System.out.println("Answer 1: " + answers[0].replace("A1:", "").trim());
				saveChat(chatroomId, true, false, answers[0].replace("A1:", "").trim()); // 확인용 임시코드저장
				String base64ImageJson = plantUml(answers[0].replace("A1:", "").trim());
				saveChat(chatroomId, true, true, base64ImageJson);
				// 글자설명 부분 저장
				System.out.println("Answer 2: " + answers[1].replace("A2:", "").trim());
				saveChat(chatroomId, true, false, answers[1].replace("A2:", "").trim());
				//System.out.println("설계단계 전체출력: \n" + response);
			}

		} else {

			saveChat(chatroomId, true, false, question);  // 질문저장
			saveChat(chatroomId, false, false, response); // 답변저장
			//System.out.println("다른단계 전체출력: \n" + response);
		}

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

	private String plantUml(String request) throws IOException {
		// PlantUML 다이어그램 요청
		String umlSource = request;

		// PlantUML 다이어그램을 byte로 생성
		SourceStringReader reader = new SourceStringReader(umlSource);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//reader.outputImage(baos).getDescription();

		// 오류검증
		if (reader.outputImage(baos) == null) {
			throw new IOException("다이어그램 생성에 실패했습니다.");
		}

		byte[] diagramBlob = baos.toByteArray();
		String base64Image = Base64.getEncoder().encodeToString(diagramBlob);

		// JSON 형태로 반환
		String jsonResponse = "{\"b64_json\":\"" + base64Image + "\"}";
		return jsonResponse;
	}
}
