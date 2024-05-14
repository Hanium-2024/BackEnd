package com.hanieum.llmproject.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class GPTServiceImplTest {

    @Autowired
    private GPTServiceImpl gptService;

    @Test
    void openAIAPIValueTest() {
        Assertions.assertThat(gptService.getToken()).isEqualTo("api key 공유되면 안돼서 아무 글 써둡니다.");
    }
}