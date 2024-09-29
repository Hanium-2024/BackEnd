package com.hanieum.llmproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class ResourceLoadService {
    @Autowired
    private ResourceLoader resourceLoader;

    public String loadPrompt() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:prompt/prompts.txt");
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }

        return contentBuilder.toString();
    }

}
