package com.hanieum.llmproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ImageOutPut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageOutPutId;
    private Long outputId;
    private String imageUrl;
}
