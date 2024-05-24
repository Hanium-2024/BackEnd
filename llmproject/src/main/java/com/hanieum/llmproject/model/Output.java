package com.hanieum.llmproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Output {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outputId;
    private Long promptId;
    private String outputContent;
    private LocalDateTime outputTime;
}
