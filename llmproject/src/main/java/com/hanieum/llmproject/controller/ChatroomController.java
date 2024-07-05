package com.hanieum.llmproject.controller;

import com.hanieum.llmproject.dto.Response;
import com.hanieum.llmproject.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
