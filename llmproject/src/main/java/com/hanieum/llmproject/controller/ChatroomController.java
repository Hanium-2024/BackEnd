package com.hanieum.llmproject.controller;

import java.util.Map;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import com.hanieum.llmproject.dto.ChatroomRequest;
import com.hanieum.llmproject.dto.ChatroomResponse;
import com.hanieum.llmproject.dto.Response;
import com.hanieum.llmproject.service.ChatroomService;

@RequiredArgsConstructor
@RestController
public class ChatroomController {
	private final ChatroomService chatroomService;

	@GetMapping("/chatrooms/titles")
	public Response<Map<Long, String>> getChatrooms(@RequestParam("loginId") String loginId) {

		return Response.success("채팅방 목록을 불러왔습니다.", chatroomService.getChatrooms(loginId));
	}

	// 채팅방 사용자 저장기능
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

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/chatrooms")
	public Response<ChatroomResponse.Detail> createChatroom(Authentication authentication,
			@Valid @RequestBody ChatroomRequest.Create request) {
		String loginId = authentication.getName();
		return Response.success("채팅방 생성 성공", chatroomService.createChatroom(loginId, request.title()));
	}
}
