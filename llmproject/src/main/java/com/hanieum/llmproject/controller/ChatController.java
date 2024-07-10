package com.hanieum.llmproject.controller;

import com.hanieum.llmproject.dto.chat.QuestionDto;
import com.hanieum.llmproject.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/main")
public class ChatController {

    private final ChatService chatService;

    // gpt응답기능
    @PostMapping(value = "/ask/{chat_room_id}", produces = "text/event-stream")
    public ResponseEntity<SseEmitter> ask(Authentication authentication,
                                          @RequestParam("categoryType") String categoryType,
                                          @PathVariable("chat_room_id") Long chat_room_id,
                                          @RequestBody QuestionDto request) {
        // 로그인정보 받아오기
        String loginId = authentication.getName();

        // 확인
        System.out.println("회원정보 = " + loginId);
        System.out.println("카테고리 = " + categoryType);
        System.out.println("요청 = " + request.getQuestion());

        // 서비스 실행
        SseEmitter response = chatService.ask(loginId, chat_room_id, categoryType, request.getQuestion());
        return ResponseEntity.ok().body(response);
    }

}