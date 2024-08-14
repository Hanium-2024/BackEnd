package com.hanieum.llmproject.controller;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hanieum.llmproject.dto.Response;
import com.hanieum.llmproject.service.ChatroomService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ChatroomController {
	private final ChatroomService chatroomService;

	@GetMapping("/chatrooms/titles")
	public Response<Map<Long, String>> getChatrooms(
		@RequestParam("loginId") String loginId,
		@RequestParam("categoryType") String categoryType) {

		return Response.success("채팅방 목록을 불러왔습니다.", chatroomService.getChatrooms(loginId, categoryType));
	}

	// 채팅방 사용자 저장기능
	@SuppressWarnings("checkstyle:RegexpSinglelineJava")
	@PutMapping("/save/{chatroomId}")
	public Response<Void> saveChatroom(Authentication authentication,
		@PathVariable("chatroomId") Long chatroomId) {

		String loginId = authentication.getName();
		chatroomService.saveChatroom(loginId, chatroomId);
		return Response.success("채팅방을 저장합니다.", null);
	}

	// 채팅방 사용자 삭제기능
	@PutMapping("/delete/{chatroomId}")
	public Response<Void> deleteChatroom(Authentication authentication,
		@PathVariable("chatroomId") Long chatroomId) {

		String loginId = authentication.getName();
		chatroomService.deleteChatroom(loginId, chatroomId);
		return Response.success("저장된 채팅방을 삭제합니다.", null);
	}
}
