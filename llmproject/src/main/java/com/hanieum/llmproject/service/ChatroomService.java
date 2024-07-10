package com.hanieum.llmproject.service;

import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import com.hanieum.llmproject.model.Category;
import com.hanieum.llmproject.model.Chatroom;
import com.hanieum.llmproject.model.User;
import com.hanieum.llmproject.repository.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ChatroomService {

    private final UserService userService;
    private final ChatroomRepository chatroomRepository;

    public Map<Long, String> getChatroomsBy(String loginId, String categoryType) {
        User user = loadUser(loginId);

        Category category = loadCategory(categoryType);

        List<Chatroom> chatroomList = chatroomRepository.findAllByUserAndCategory(user, category);

        return Chatroom.getChatrooms(chatroomList);
    }

    private User loadUser(String loginId) {
        return userService.findUserByLoginId(loginId);
    }

    // CategoryService를 따로 만들어 책임 분리 고려해야 함.
    private Category loadCategory(String categoryString) {
        validateCategory(categoryString);

        return Category.fromString(categoryString);
    }

    private void validateCategory(String categoryType) {
        if(!Category.isValid(categoryType)) {
            throw new CustomException(ErrorCode.CATEGORY_NOT_VALID);
        }
    }

    // ---채팅 자동저장 관련---
    // 채팅방 찾기
    public Chatroom find_chatroom(Long chatroomId) {
        return chatroomRepository.findById(chatroomId).orElse(null);
    }

    // 찾고 없으면 새채팅방 자동생성
    public Long find_chatroom(String userId, Long chatroomId, String categoryType, String title) {
        Category category = loadCategory(categoryType);
        User user = loadUser(userId);

        // 채팅방 (사용자, 카테고리, 채팅방아이디 불일치 -> 카테고리항목에 새로운 채팅방생성)
        Chatroom chatroom = chatroomRepository.findByUserAndCategoryAndId(user,category,chatroomId)
                .orElseGet(()->createChatRoom(userId, category, title));
        return chatroom.getChatroomId();
    }

    // 채팅방 없을시 생성
    private Chatroom createChatRoom(String userId, Category categoryType, String title) {
        User user = loadUser(userId);
        Chatroom chatroom = new Chatroom(user, categoryType, title);
        System.out.println("채팅방을 생성합니다.");
        chatroomRepository.save(chatroom);
        return chatroom;
    }

    // ---채팅 사용자 저장 관련---
    @Transactional
    public void chatroomSave(String loginId, Long chat_room_id) {
        // 채팅방 찾기
        User user = loadUser(loginId);
        Chatroom chatroom = chatroomRepository.findByUserAndId(user,chat_room_id)
                .orElseThrow(()-> new IllegalArgumentException("채팅방을 찾을수없음"));

        // 저장상태 업데이트
        if (!chatroom.isSaved()) {
            chatroom.setSaved(true);
        }
    }

    @Transactional
    public void chatroomDelete(String loginId, Long chat_room_id) {
        // 채팅방 찾기
        User user = loadUser(loginId);
        Chatroom chatroom = chatroomRepository.findByUserAndId(user,chat_room_id)
                .orElseThrow(()-> new IllegalArgumentException("채팅방을 찾을수없음"));

        // 저장상태 업데이트
        if (chatroom.isSaved()) {
            chatroom.setSaved(false);
        }
    }

}
