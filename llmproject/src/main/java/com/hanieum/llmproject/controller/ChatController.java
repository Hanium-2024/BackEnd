package com.hanieum.llmproject.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.hanieum.llmproject.dto.chat.ChatRequest;
import com.hanieum.llmproject.dto.chat.ChatResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanieum.llmproject.dto.Response;
import com.hanieum.llmproject.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

	private final ChatService chatService;

	// gpt응답기능
	@PostMapping(value = "/main/ask/{chatroomId}")
	public Response<String> ask(Authentication authentication,
								@RequestParam("categoryType") String categoryType,
								@PathVariable("chatroomId") Long chatroomId,
								@RequestBody ChatRequest.Common request) throws IOException {
		// 로그인정보 받아오기
		String loginId = authentication.getName();

		// 확인
		System.out.println("회원정보 = " + loginId);
		System.out.println("카테고리 = " + categoryType);
		System.out.println("요청 = " + request.question());

		// 서비스 실행
		return Response.success("질문성공", chatService.ask(chatroomId, categoryType, request.question()));
	}

	@PostMapping("/main/ask/{chatroomId}/retrospect")
	public Response<?> askRetrospect(Authentication authentication,
									@PathVariable("chatroomId") Long chatroomId,
									@RequestBody List<ChatRequest.Retrospect> request) {
		return Response.success("질문 성공", chatService.askRetrospect(chatroomId, request));
	}

	@GetMapping("/chats/{chatroomId}")
	public Response<List<String>> getChats(@PathVariable("chatroomId") Long chatroomId,
										   @RequestParam("categoryType") String categoryType) {
		return Response.success("채팅을 불러왔습니다.", chatService.getChats(chatroomId, categoryType));
	}

	// 회고에서 사용할 채팅 선택기능
	@PostMapping("/chat/retrospect/{chatId}")
	public Response<Void> retrospectChat(@PathVariable("chatId") Long chatId) {

		chatService.checkRetrospectChat(chatId);
		return Response.success("회고에서 사용할 채팅을 선택했습니다.", null);
	}

	// 회고에서 사용할 채팅목록 불러오기
	@GetMapping("/chats/retrospect/{chatroomId}")
	public Response<List<Map<String, String>>> getRetrospectChats(@PathVariable("chatroomId") Long chatroomId) {
		return Response.success("회고에서 사용할 채팅목록을 불러왔습니다.", chatService.getRetrospectChats(chatroomId));
	}

	// 회고 단계 히스토리 불러오기
	@GetMapping("/chatrooms/{chatroomId}/retrospect")
	public Response<List<ChatResponse.RetrospectHistory>> getRetrospects(@PathVariable("chatroomId") Long chatroomId) {
		return Response.success("회고 단계 히스토리를 불러왔습니다.", chatService.getRetrospectHistories(chatroomId));
	}
}
