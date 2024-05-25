package com.hanieum.llmproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Getter
@Slf4j
@Service
@NoArgsConstructor
public class GPTServiceImpl implements GPTService {
    @Value("#{'${openai.key}'.trim()}")
    private String token;

    public JsonNode callGPT(String userMsg) throws JsonProcessingException {
        final String url = "https://api.openai.com/v1/chat/completions";

        // Http 헤더 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        ObjectMapper objectMapper = new ObjectMapper();

        // https://platform.openai.com/docs/api-reference/making-requests Making request 부분 참고

        // 응답받을 gpt model 작성
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", "gpt-3.5-turbo");

        // messages 부분 작성
        List<Map<String, String>> messages = new ArrayList<>();

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", userMsg);
        messages.add(userMessage);

        Map<String, String> assistantMessage = new HashMap<>();
        assistantMessage.put("role", "system");
        assistantMessage.put("content", "you are a friendly ai");
        messages.add(assistantMessage);

        bodyMap.put("messages", messages);

        String body = objectMapper.writeValueAsString(bodyMap);

        // 헤더와 모델이나 메시지 담은 바디로 HttpEntity 만들기
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        // Post 타입으로 요청
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        return objectMapper.readTree(response.getBody());
    }

    @Override
    public ResponseEntity<?> getAssisantMsg(String userMsg) throws JsonProcessingException {
        JsonNode jsonNode = callGPT(userMsg);
        String content = jsonNode.path("choices").get(0).path("message").path("content").asText();

        return ResponseEntity.status(HttpStatus.OK).body(content);
    }


}
