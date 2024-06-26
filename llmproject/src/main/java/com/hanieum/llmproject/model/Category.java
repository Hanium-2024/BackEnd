package com.hanieum.llmproject.model;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Long id;
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;
}
