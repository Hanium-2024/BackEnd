package com.hanieum.llmproject.service;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

import com.hanieum.llmproject.dto.chat.ChatRequest;
import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import com.hanieum.llmproject.model.Category;
import net.sourceforge.plantuml.SourceStringReader;
import org.springframework.stereotype.Service;

import com.hanieum.llmproject.dto.chat.ChatMessage;
import com.hanieum.llmproject.model.Chat;
import com.hanieum.llmproject.model.Chatroom;
import com.hanieum.llmproject.repository.ChatRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

	private final ChatRepository chatRepository;
	private final ChatroomService chatroomService;
	private final GPTService gptService;

	// 채팅내역 자동저장기능 (사용자답변, gpt답변분리)
	private void saveChat(Long chatroomId, Category category, boolean isUserMessage, boolean isImage, String message) {

		Chatroom chatroom = chatroomService.findChatroom(chatroomId);

		Chat chat = new Chat(chatroom, category, isUserMessage, isImage, message);

		chatRepository.save(chat);
	}

	// 이전답변+새답변 gpt에 재입력 (과거 대화 기억해서 답변시키기)
	private List<ChatMessage> compositeMessage(Long chatroomId, Category category, String question) {

		List<ChatMessage> messages = new ArrayList<>();

		Chatroom chatroom = chatroomService.findChatroom(chatroomId);

		// categoryType과 동일한 타입의 채팅목록만 불러오기
		List<Chat> chatHistory = chatRepository.findAllByChatroomAndCategory(chatroom, category);

		for (Chat chat : chatHistory) {
			messages.add(new ChatMessage(chat.isUserMessage() ? "user" : "assistant", chat.getMessage()));
		}

		// 새 메세지 추가
		messages.add(new ChatMessage("user", question));
		return messages;
	}

	// 채팅목록 카테고리별로 불러오기
	public List<String> getChats(Long chatroomId, String categoryType) {
		var chatroom = chatroomService.findChatroom(chatroomId);

		Category category = loadCategory(categoryType);

		return chatRepository.findAllByChatroomAndCategory(chatroom, category)
			.stream()
			.sorted(Comparator.comparing(Chat::getOutputTime))
			.map(Chat::getMessage)
			.toList();
	}


	public Category loadCategory(String categoryString) {
		validateCategory(categoryString);

		return Category.fromString(categoryString);
	}

	private void validateCategory(String categoryType) {
		if (!Category.isValid(categoryType)) {
			throw new CustomException(ErrorCode.CATEGORY_NOT_VALID);
		}
	}

	@Transactional
	public String askRetrospect(Long chatroomId, List<ChatRequest.Retrospect> retrospects) {
		chatroomService.findChatroom(chatroomId);

		List<ChatMessage> messages = new ArrayList<>();

		// todo: title 과 KPT 합치기 작업 -> question
		for (ChatRequest.Retrospect retrospect : retrospects) {
            String question = "회고할 내용 : " + retrospect.topic() + "\n" +
                    "Keep 항목 : " + retrospect.keepContent() + "\n" +
                    "Problem 항목 : " + retrospect.problemContent() + "\n" +
                    "Try 항목 : " + retrospect.tryContent() + "\n\n";

			messages.add(new ChatMessage("user", question));
		}

		String response = gptService.requestGPT(messages, Category.RETROSPECT);

		return response;
	}

	// gpt질문하는 메인기능
	public String ask(Long chatroomId, String categoryType, String question) throws IOException {
		chatroomService.findChatroom(chatroomId);

		// 카테고리 불러오기
		Category category = loadCategory(categoryType);

		// gpt요청데이터 셋팅
//		ChatRequestDto chatRequestDto = ChatRequestDto.builder().
//			model(ChatGptConfig.CHAT_MODEL).
//			maxTokens(ChatGptConfig.MAX_TOKEN).
//			temperature(ChatGptConfig.TEMPERATURE).
//			messages(gptService.applyPromptEngineering(compositeMessage(chatroomId, category, question), category)).
//			build();

		// gpt 응답
//		String response = gptService.gptRequest(chatRequestDto);
		String response = gptService.requestGPT(compositeMessage(chatroomId, category, question), category);

		// 디자인카테고리의 답변일때(DESIGN)
		if (category== Category.DESIGN) {

			// 사용자질문 저장
			saveChat(chatroomId, category, true, false, question);

			// 응답형식 a1 , a2 분리
			String[] answers = response.split("A2:");

			// A1: 없음, A2: text 일때
			if (answers[0].replace("A1:", "").trim().equals("없음")) {
				//System.out.println("Answer 1: 생성할 plant uml코드가 없습니다.");
				//System.out.println("Answer 2: " + answers[1].replace("A2:", "").trim());
				saveChat(chatroomId, category, false, false, answers[1].replace("A2:", "").trim());
				return answers[1].replace("A2:", "").trim();

			// A1: plantUml코드, A2: text 일때
			} else {
				// plantuml로 코드전달 및 저장
				//System.out.println("Answer 1: " + answers[0].replace("A1:", "").trim());
				// saveChat(chatroomId, true, false, answers[0].replace("A1:", "").trim()); // 확인용 임시코드저장
				String base64ImageJson = plantUml(answers[0].replace("A1:", "").trim());
				saveChat(chatroomId, category,false, true, base64ImageJson);

				// text설명 부분 저장
				//System.out.println("Answer 2: " + answers[1].replace("A2:", "").trim());
				saveChat(chatroomId, category,false, false, answers[1].replace("A2:", "").trim());
				return base64ImageJson + "\n\n" + answers[1].replace("A2:", "").trim();
			}

		// PLAN, CODE 카테고리의 답변일때
		} else {
			saveChat(chatroomId, category,true, false, question);  // 질문저장
			saveChat(chatroomId, category,false, false, response); // 답변저장
			return response;
		}
	}

	// 설계도생성기능
	private String plantUml(String request) throws IOException {
		// PlantUML 다이어그램 요청
		String umlSource = request;

		// PlantUML 다이어그램을 byte로 생성
		SourceStringReader reader = new SourceStringReader(umlSource);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		reader.outputImage(baos).getDescription();

		// 오류검증
		if (reader.outputImage(baos) == null) {
			throw new IOException("다이어그램 생성에 실패했습니다.");
		}

		byte[] diagramBlob = baos.toByteArray();
		String base64Image = Base64.getEncoder().encodeToString(diagramBlob);

		// JSON 형태로 반환
		String jsonResponse = "{\"b64_json\":\"" + base64Image + "\"}";
		return jsonResponse;

//		// PlantUML 다이어그램을 이미지 파일로 생성
//		SourceStringReader reader = new SourceStringReader(umlSource);
//		File outputFile = new File("diagram.png");
//		try (OutputStream png = new FileOutputStream(outputFile)) {
//			// 다이어그램 생성
//			reader.outputImage(png).getDescription();
//		}
//
//		System.out.println("다이어그램이 생성되었습니다: " + outputFile.getAbsolutePath());
//		return "t";
	}

	// 회고를위한 채팅선택기능
	@Transactional
	public void checkRetrospectChat(Long chatId) {
		Chat chat = chatRepository.findById(chatId).
				orElseThrow(() -> new CustomException(ErrorCode.CHAT_NOT_FOUND));
		if (chat != null) {
			chat.setRetrospect(!chat.isRetrospect());
		}
	}

	// 회고를 위해 선택된 채팅목록 불러오기 기능
	public List<String> getRetrospectChats(Long chatroomId) {
		Chatroom chatroom = chatroomService.findChatroom(chatroomId);
		return chatRepository.findAllByChatroomAndRetrospect(chatroom, true)
				.stream()
				.sorted(Comparator.comparing(Chat::getOutputTime))
				.map(Chat::getMessage)
				.toList();
	}
}
