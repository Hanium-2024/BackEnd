package com.hanieum.llmproject.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanieum.llmproject.dto.chat.ChatMessage;
import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import com.hanieum.llmproject.model.Category;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class GPTService {
    private static final String CHAT_MODEL = "gpt-4o-mini";
    private static final String CHAT_MODEL_DESIGN = "ft:gpt-4o-mini-2024-07-18:hanieumllm::ACsKH7aj";
    private static final Integer MAX_TOKEN = 4000;
    private static final Double TEMPERATURE = 0.6;

    @Value("#{'${openai.key}'.trim()}")
    private String token;

    private final ChatroomService chatroomService;
    private final ResourceLoadService resourceLoadService;

    // 프롬프트엔지니어링 부분
    public List<ChatMessage> applyPromptEngineering(List<ChatMessage> messages, Category category) throws IOException {

        // 카테고리별로 프롬프트 엔지니어링 하기
        // 공통 시스템 메세지
        messages.add(0, new ChatMessage("system", "너는 llm 코딩 자동화 어시스턴트 도구야. 개발자에게 친절하게 도움을 줘야해."));
        //messages.add(1,new ChatMessage("system", "주로 초보개발자가 사용하기때문에 자세하고 친절하게 알려줘야해."));

        if (category == Category.PLAN) { // 계획단계에 맞는 적합한 답변을 지시
            messages.add(1, new ChatMessage("system", resourceLoadService.loadPromptPLAN()));
            // 공통프롬프트
//            messages.add(1, new ChatMessage("system", "너는 소프트웨어 개발의 계획단계에 필요한 기능명세서를 출력해주는 모델이야."));
//            messages.add(2, new ChatMessage("system", "소프트웨어 개발의 계획단계에 필요한 기능명세서 요청과 관련없는 질문에 대해서는 사용자에게 질문을 재입력하도록 요구해."));
//            messages.add(3, new ChatMessage("system", "프로그램을 개발하기위해 필요한 기능명세서를 출력해주는거니까 표형태로 가시성있게 답변을 해줘야해."));
//            messages.add(4, new ChatMessage("system", "요청받은것에 대해 최대한 상세하게 많은 기능을 명세해줘야해."));
//
//            // 부가프롬프트
//            messages.add(5, new ChatMessage("system", "기능 명세서를 작성하기에 부족한 정보를 제시한다면 너가 스스로 질문을 되물어서 자세한 정보를 받아내야해."));


        } else if (category == Category.DESIGN) {
            messages.add(1, new ChatMessage("system", resourceLoadService.loadPromptDesign()));
//            // 공통 프롬프트 메시지
//            messages.add(1, new ChatMessage("system", "너는 사용자의 요구사항을 erd 다이어그램으로 바꿔주는 소프트웨어 개발의 설계단계의 모델이야."));
//            messages.add(2, new ChatMessage("system", "너가 스스로 적합한 다이어그램 종류를 골라서 만들어줘."));
//            messages.add(3, new ChatMessage("system", "사용자가 코드를 보내거나 어떠한주제에대한 설명등을 제시하면, 그걸 Plant Uml문법으로 바꿔서 출력해줘."));
//            messages.add(4, new ChatMessage("system", "출력 형식은 무조건" +
//                    "A1: plant uml 코드\n" +
//                    "\n" +
//                    //"---\n" +
//                    "\n" +
//                    "A2: 글자설명\n" +
//                    " 이렇게 보내줘"));
//            messages.add(5, new ChatMessage("system", "plant uml코드는 반드시 오류없는 코드를 보내줘"));
//            messages.add(6, new ChatMessage("system", "코드생성을 하기위한 정보가 부족하다면 일단 A1에 부족한정보에서 임의로 plant uml 코드를 작성해줘. 그리고 A2에 답변으로 설명을 작성해. 그리고 더 자세한 답변을 얻기위한 형식을 맨아래 알려줘."));
//            messages.add(7, new ChatMessage("system", "소프트웨어 개발의 설계도작성, 다이어그램 작성에 관한 정보가 아니면 출력형식을 유지하되, A1에는 \"없음\"을 작성하고, A2에는 재입력을 위해 필요한 정보를 요구해."));
//            messages.add(8, new ChatMessage("system", "다이어그램을 생성할 때 NanumGothic 폰트를 사용해야 해. 이를 위해서 'skinparam defaultFontName \"NanumGothic\"을 무조건 포함해'"));

        } else if (category == Category.CODE) { // 코딩단계에 맞는 적합한 답변을 지시
            messages.add(1, new ChatMessage("system", resourceLoadService.loadPromptCode()));
            // 공통 프롬프트 메시지
//            messages.add(1, new ChatMessage("system", "코드를 보완해달라거나 작성해달라는 요청에는 무조건 코드로 답해주어야 해. 필요에 따라 추가로 서술할 수 있어."));
//            messages.add(2, new ChatMessage("system", "코드를 작성해줄 때는 코드의 가독성이 생기도록 무조건 '\n'를 사용해서 개행을 만들어야해."));
//            messages.add(3, new ChatMessage("system", "최대한 최적화된 코드를 작성해야하고, 답변은 최대한 코드로, 필요시 주석으로 설명을 달아서 답변해."));
//            messages.add(4, new ChatMessage("system", "사용자가 프로그래밍 언어를 명시하지 않았거나 프로그래밍 언어를 유추할 수 없다면 임의로 작성하지 말고 사용자에게 어떤 언어를 사용하여 코드를 작성해야하냐고 다시 물어서 정보를 얻어."));
//            messages.add(5, new ChatMessage("system", "코드는 오류가 나지 않는, 안전하고 완전한 코드로 응답해주어야 해."));
//
//            // 부가 프롬프트 메시지
//            messages.add(6, new ChatMessage("system", "코드 생성이나 리팩토링 등 그 외 코드와 관련되지 않은 요청에는 사용자에게 관련되지 않은 요청이라고 응답해주고 다시 코드와 관련된 요청을 달라고 응답해."));
//            messages.add(7, new ChatMessage("system", "코드를 생성하는 요청에서 코드를 생성하기에 정보가 부족하거나 없다면 코드를 완성시키기 위해 필요한 정보를 사용자에게 요청해야해"));
//            messages.add(8, new ChatMessage("system", "코드를 생성했을 때, 코드에서 4자리의 공백은 하나의 '\t'로 치환해서 값을 응답해줘"));

        } else if (category == Category.RETROSPECT) { // 회고 내용에 맞는 적합한 답변을 지시

            String prompt = resourceLoadService.loadPromptRetrospect();

            // 개별 시스템 메시지 추가
            messages.add(1, new ChatMessage("system", prompt));
            messages.add(2, new ChatMessage("system", "보고서형태로 출력해줘"));
        }

        return messages;
    }

    public String requestGPT(List<ChatMessage> messages, Category category) throws IOException {
        List<ChatMessage> chatMessages = applyPromptEngineering(messages, category);
        GptRequest gptRequest = new GptRequest(chatMessages);

        WebClient client = WebClient.create("https://api.openai.com/v1");

        // gpt요청
        String response = client.post()
                .uri("/chat/completions")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromValue(gptRequest))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // 응답에서 content 부분 추출
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode choicesNode = root.path("choices").get(0);
            String content = choicesNode.path("message").path("content").asText();

            return content;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.JSON_PARSE_ERROR);
        }
    }

	@Getter
    private static class GptRequest {
        private String model;
        @JsonProperty("max_tokens")
        private Integer maxTokens;
        private Double temperature;
        private List<ChatMessage> messages;

        private GptRequest(List<ChatMessage> messages) {
            model = CHAT_MODEL;
            maxTokens = MAX_TOKEN;
            temperature = TEMPERATURE;
            this.messages = messages;
        }
    }
}
