package com.hanieum.llmproject.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanieum.llmproject.config.ChatGptConfig;
import com.hanieum.llmproject.dto.Response;
import com.hanieum.llmproject.dto.chat.ChatMessage;
import com.hanieum.llmproject.dto.chat.ChatRequestDto;
import com.hanieum.llmproject.dto.chat.ChatStreamResponseDto;
import com.hanieum.llmproject.dto.chat.QuestionDto;
import com.hanieum.llmproject.model.Chat;
import com.hanieum.llmproject.model.Chatroom;
import com.hanieum.llmproject.model.User;
import com.hanieum.llmproject.repository.ChatRepository;
import com.hanieum.llmproject.repository.ChatroomRepository;
import com.hanieum.llmproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    @Value("#{'${openai.key}'.trim()}")
    private String token;

    private final ChatRepository chatRepository;
    private final ChatroomService chatroomService;

    // 채팅내역 자동저장기능 (사용자답변, gpt답변분리)
    private void saveChat(Long chatroomId, boolean isUserMessage, String message){

        Chatroom chatroom = chatroomService.findChatroom(chatroomId);
        if (chatroom == null){ throw new IllegalArgumentException("채팅방을 찾을수없음.");}

        Chat chat = new Chat(chatroom, isUserMessage, message);

        chatRepository.save(chat);
    }

    // 이전답변+새답변 gpt에 재입력 (과거 대화 기억해서 답변시키기)
    private List<ChatMessage> compositeMessage(Long chatroomId, String question){

        List<ChatMessage> messages = new ArrayList<>();

        // 이전메세지 가져오기
        Chatroom chatroom = chatroomService.findChatroom(chatroomId);
        if (chatroom == null){ throw new NullPointerException("채팅방을 찾을수없음.");}

        List<Chat> chatHistory = chatRepository.findAllByChatroom(chatroom);

        for (Chat chat : chatHistory) {
            messages.add(new ChatMessage(chat.isUserMessage() ? "user" : "assistant", chat.getMessage()));
            System.out.println("이전채팅 = " + chat.getMessage());
        }

        // 새 메세지 추가
        messages.add(new ChatMessage("user", question));
        return messages;
    }

    // 채팅방 제목설정
    private String createTitle(String question) {
        // 문장이 20보다 작은경우 처리
        int endIndex = Math.min(question.length(), 10);
        // 0 ~ 20 문자열 추출
        String title = question.substring(0, endIndex);
        System.out.println("substring = " + title);
        return title;
    }

    public List<String> getChats(Long chatroomId) {
        var chatroom = chatroomService.findChatroom(chatroomId);

        return chatRepository.findAllByChatroom(chatroom)
                .stream()
                .sorted(Comparator.comparing(Chat::getOutputTime))
                .map(Chat::getMessage)
                .toList();
    }

    // sse응답기능
    public SseEmitter ask(String loginId, Long chatroomId, String categoryType, String question) {

        // 질문 기반 채팅방제목생성
        String title = createTitle(question);

        // 채팅방 찾기(없으면 생성)
        Long chatRoomId = chatroomService.findOrCreateChatroom(loginId, chatroomId, categoryType, title);

        // gpt요청데이터 셋팅
        ChatRequestDto chatRequestDto = ChatRequestDto.builder().
                model(ChatGptConfig.CHAT_MODEL).
                maxTokens(ChatGptConfig.MAX_TOKEN).
                temperature(ChatGptConfig.TEMPERATURE).
                stream(true).
                messages(compositeMessage(chatRoomId, question)).
                        build();

        // 기타셋팅
        StringBuffer sb = new StringBuffer(); // 텍스트한번에 저장할 버퍼
        SseEmitter emitter = new SseEmitter((long) (5 * 60 * 1000)); // SseEmitter(기본시간)
        WebClient client = WebClient.create("https://api.openai.com/v1");

        // gpt요청
        client.post().uri("/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromValue(chatRequestDto))
                .exchangeToFlux(response -> response.bodyToFlux(String.class))
                .doOnNext(line -> {
                    try {
                        if (line.equals("[DONE]")) {    // 응답 종료시
                            // 저장
                            emitter.send("chat_room_id: "+chatRoomId );
                            saveChat(chatRoomId,true, question);      // 사용자질문저장
                            saveChat(chatRoomId,false,sb.toString()); // gpt답변저장
                            //emitter.send("{\"content\": \"" + sb.toString() + "\"}"); // JSON 형식으로 전송
                            emitter.complete();

                            // 결과 확인용
                            System.out.println("sb = " + sb);
                        } else {    // 응답 진행중
                            ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
                            ChatStreamResponseDto streamDto = mapper.readValue(line,ChatStreamResponseDto.class);
                            ChatStreamResponseDto.Choice.Delta delta = streamDto.getChoices().get(0).getDelta();

                            if (delta!=null && delta.getContent()!=null){
                                sb.append(delta.getContent());      // 버퍼에 sse답변모으기
                                //emitter.send(delta.getContent());   // 프론트로 text만 출력시킴
                                emitter.send("{\"content\": \"" + delta.getContent() + "\"}"); // json형식으로 전송
                                System.out.println("Sending to frontend: " + delta.getContent());
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .doOnError(emitter::completeWithError)
                .doOnComplete(emitter::complete)
                .subscribe();

        return emitter;
    }
}
