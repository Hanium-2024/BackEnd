package com.hanieum.llmproject.service;

import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DallEService {
    private final OpenAiService openAiService;

    public String generateImage(String question) {
        CreateImageRequest createImageRequest = CreateImageRequest.builder()
                .prompt(question)
                .n(1)
                .size("512x512")
                .responseFormat("b64_json")
                .build();

        return openAiService.createImage(createImageRequest).getData().get(0).getB64Json();
    }
}
