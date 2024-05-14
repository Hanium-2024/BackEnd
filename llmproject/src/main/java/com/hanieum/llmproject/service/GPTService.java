package com.hanieum.llmproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface GPTService {
    ResponseEntity<?> getAssisantMsg(String userMsg) throws JsonProcessingException;
}
