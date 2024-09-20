package com.hanieum.llmproject.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import com.hanieum.llmproject.model.Category;
import com.hanieum.llmproject.model.Chatroom;
import com.hanieum.llmproject.model.User;
import com.hanieum.llmproject.repository.ChatroomRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatroomService {

	private final UserService userService;
	private final ChatroomRepository chatroomRepository;

	public Map<Long, String> getChatrooms(String loginId) {
		User user = loadUser(loginId);

		List<Chatroom> chatroomList = chatroomRepository.findAllByUser(user);

		return Chatroom.getSavedChatrooms(chatroomList);
	}

	private User loadUser(String loginId) {
		return userService.findUserByLoginId(loginId);
	}

	// ---채팅 자동저장 관련---
	// 채팅방 찾기
	public Chatroom findChatroom(Long chatroomId) {
		return chatroomRepository.findById(chatroomId)
			.orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
	}

	// 찾고 없으면 새채팅방 자동생성(채팅방없을시createChatroom호출)
	public Long findOrCreateChatroom(String userId, Long chatroomId, String title) {
		User user = loadUser(userId);

		// 채팅방 (사용자, 카테고리, 채팅방아이디 불일치 -> 카테고리항목에 새로운 채팅방생성)
		Chatroom chatroom = chatroomRepository.findByUserAndId(user, chatroomId)
			.orElseGet(() -> createChatRoom(userId, chatroomId, title));
		return chatroom.getChatroomId();
	}

	// 채팅방 없을시 생성
	private Chatroom createChatRoom(String userId, Long chatroomId, String title) {
		User user = loadUser(userId);
		Chatroom chatroom = new Chatroom(user, chatroomId, title);
		System.out.println("채팅방을 생성합니다.");
		chatroomRepository.save(chatroom);
		return chatroom;
	}

	// ---채팅 사용자 저장 관련---
	@Transactional
	public void saveChatroom(String loginId, Long chatroomId) {
		// 채팅방 찾기
		User user = loadUser(loginId);
		Chatroom chatroom = chatroomRepository.findByUserAndId(user, chatroomId)
			.orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

		// 저장상태 업데이트
		if (!chatroom.isSaved()) {
			chatroom.setSaved(true);
		}
	}

	@Transactional
	public void deleteChatroom(String loginId, Long chatroomId) {
		// 채팅방 찾기
		User user = loadUser(loginId);
		Chatroom chatroom = chatroomRepository.findByUserAndId(user, chatroomId)
			.orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

		// 저장상태 업데이트
		if (chatroom.isSaved()) {
			chatroom.setSaved(false);
		}
	}

}
