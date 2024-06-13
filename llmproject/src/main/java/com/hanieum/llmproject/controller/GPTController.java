package com.hanieum.llmproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanieum.llmproject.service.GPTService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GPTController {

    private final GPTService gptService;

    @PostMapping ("/question") // 서버 안 띄운 상태에서는 http://localhost:8080/question 로 요청
    public ResponseEntity<?> sendQuestion(@RequestBody String msg) throws JsonProcessingException {
        return gptService.getAssisantMsg(msg);
    }
}
