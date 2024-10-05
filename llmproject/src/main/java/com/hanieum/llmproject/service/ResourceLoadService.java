package com.hanieum.llmproject.service;

import org.jetbrains.annotations.NotNull;
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

    public String loadPromptPLAN() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:prompt/prompt_plan.txt");
        return getString(resource);
    }

    public String loadPromptDesign() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:prompt/prompt_design.txt");
        return getString(resource);
    }

    public String loadPromptCode() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:prompt/prompt_code.txt");
        return getString(resource);
    }



    public String loadPromptRetrospect() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:prompt/prompts.txt");
        return getString(resource);
    }

    @NotNull
    private String getString(Resource resource) throws IOException {
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
