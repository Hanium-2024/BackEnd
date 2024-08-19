package com.hanieum.llmproject.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.hanieum.llmproject.dto.Response;
import com.hanieum.llmproject.dto.chat.QuestionDto;
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
		@RequestBody QuestionDto request) {
		// 로그인정보 받아오기
		String loginId = authentication.getName();

		// 확인
		System.out.println("회원정보 = " + loginId);
		System.out.println("카테고리 = " + categoryType);
		System.out.println("요청 = " + request.getQuestion());

		// 서비스 실행
		return Response.success("질문성공", chatService.ask(loginId, chatroomId, categoryType, request.getQuestion()));
	}

	@PostMapping("/main/design/{chatroomId}")
	public Response<String> askImage(@RequestBody String question,
		@PathVariable("chatroomId") Long chatroomId) {
		return Response.success("설계도 이미지 생성 완료", chatService.askImage(chatroomId, question));
	}

	@GetMapping("/chats/{chatroomId}")
	public Response<List<String>> getChats(@PathVariable("chatroomId") Long chatroomId) {
		return Response.success("채팅을 불러왔습니다.", chatService.getChats(chatroomId));
	}
}
