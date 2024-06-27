package com.hanieum.llmproject.service;

import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import com.hanieum.llmproject.model.Category;
import com.hanieum.llmproject.model.Chatroom;
import com.hanieum.llmproject.model.User;
import com.hanieum.llmproject.repository.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
