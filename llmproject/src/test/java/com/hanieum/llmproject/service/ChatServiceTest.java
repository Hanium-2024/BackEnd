package com.hanieum.llmproject.service;

import com.hanieum.llmproject.model.Chat;
import com.hanieum.llmproject.model.Chatroom;
import com.hanieum.llmproject.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatroomService chatroomService;

    @InjectMocks
    private ChatService chatService;

    private Chatroom chatroom;

    @Test
    void 채팅_내역_불러오기_성공() {
        // given
        chatroom = mock(Chatroom.class);

        Chat chat1 = new Chat(chatroom, true, "질문 1");
        setField(chat1, "outputTime", LocalDateTime.of(2024, 7, 20, 10, 15));

        Chat chat2 = new Chat(chatroom, false, "답변 1");
        setField(chat2, "outputTime", LocalDateTime.of(2024, 7, 20, 10, 30));

        Chat chat3 = new Chat(chatroom, true, "질문 2");
        setField(chat3, "outputTime", LocalDateTime.of(2024, 7, 21, 5, 10));

        Chat chat4 = new Chat(chatroom, false, "답변 2");
        setField(chat4, "outputTime", LocalDateTime.of(2024, 7, 21, 5, 20));

        when(chatroomService.findChatroom(1L)).thenReturn(chatroom);
        when(chatRepository.findAllByChatroom(chatroom)).thenReturn(Arrays.asList(chat3, chat2, chat4, chat1));

        // when
        var chats = chatService.getChats(1L);

        // then
        var expected = Arrays.asList("질문 1", "답변 1", "질문 2", "답변 2");
        assertEquals(expected, chats);

        verify(chatroomService, times(1)).findChatroom(1L);
        verify(chatRepository, times(1)).findAllByChatroom(chatroom);
    }
}