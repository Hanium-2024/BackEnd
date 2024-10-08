package com.hanieum.llmproject.service;

import java.util.List;
import java.util.Map;

import com.hanieum.llmproject.dto.ChatroomResponse;
import com.hanieum.llmproject.model.Retrospect;
import com.hanieum.llmproject.model.RetrospectQuestion;
import com.hanieum.llmproject.repository.RetrospectQuestionRepository;
import com.hanieum.llmproject.repository.RetrospectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import com.hanieum.llmproject.model.Chatroom;
import com.hanieum.llmproject.model.User;
import com.hanieum.llmproject.repository.ChatroomRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatroomService {

	private final UserService userService;
	private final ChatroomRepository chatroomRepository;
	private final RetrospectRepository retrospectRepository;
	private final RetrospectQuestionRepository retrospectQuestionRepository;

	public Map<Long, String> getChatrooms(String loginId) {
		User user = loadUser(loginId);

		List<Chatroom> chatroomList = chatroomRepository.findAllByUser(user);

		return Chatroom.getSavedChatrooms(chatroomList);
	}

	private User loadUser(String loginId) {
		return userService.findUserByLoginId(loginId);
	}

	// 채팅방 찾기
	public Chatroom findChatroom(Long chatroomId) {
		return chatroomRepository.findById(chatroomId)
			.orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
	}

	@Transactional
	public ChatroomResponse.Detail createChatroom(String loginId, String title) {
		User user = loadUser(loginId);
		Chatroom chatroom = new Chatroom(user, title);

		System.out.println("채팅방을 생성합니다.");

		Chatroom savedChatroom = chatroomRepository.save(chatroom);
		return new ChatroomResponse.Detail(savedChatroom.getChatroomId(), title);
	}

	@Transactional
	public void deleteChatroom(String loginId, Long chatroomId) {
		// 채팅방 찾기
		User user = loadUser(loginId);
		Chatroom chatroom = chatroomRepository.findByUserAndId(user, chatroomId)
			.orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

		// 회고 카테고리 채팅 삭제
		deleteRetrospectChat(chatroom);

		// TODO cascade삭제
		// 채팅삭제
		// 채팅방삭제
		chatroomRepository.delete(chatroom);
	}

	private void deleteRetrospectChat(Chatroom chatroom) {
		List<Retrospect> retrospects = retrospectRepository.findAllByChatroom(chatroom);

		for (Retrospect retrospect : retrospects) {
			List<RetrospectQuestion> retrospectQuestions = retrospectQuestionRepository.findAllByRetrospect(retrospect);

            retrospectQuestionRepository.deleteAll(retrospectQuestions);
		}

		retrospectRepository.deleteAll(retrospects);
	}

}
