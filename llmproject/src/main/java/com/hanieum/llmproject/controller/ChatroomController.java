package com.hanieum.llmproject.controller;

import com.hanieum.llmproject.dto.Response;
import com.hanieum.llmproject.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class ChatroomController {
    private final ChatroomService chatroomService;

    @GetMapping("/chatrooms/titles")
    public Response<Map<Long, String>> getChatroomsBy(
        @RequestParam("loginId") String loginId,
        @RequestParam("categoryType") String categoryType) {

        return Response.success("채팅방 목록을 불러왔습니다.", chatroomService.getChatroomsBy(loginId, categoryType));
    }

    // 채팅방 사용자 저장기능
    @PutMapping("/save/{chat_room_id}")
    public Response<Void> saveChatRoom(Authentication authentication,
                                       @PathVariable("chat_room_id") Long chat_room_id) {

        String loginId = authentication.getName();
        chatroomService.chatroomSave(loginId, chat_room_id);
        return Response.success("채팅방을 저장합니다.", null);
    }
    // 채팅방 사용자 삭제기능
    @PutMapping("/delete/{chat_room_id}")
    public Response<Void> deleteChatRoom(Authentication authentication,
                                       @PathVariable("chat_room_id") Long chat_room_id) {

        String loginId = authentication.getName();
        chatroomService.chatroomDelete(loginId, chat_room_id);
        return Response.success("저장된 채팅방을 삭제합니다.", null);
    }
}
