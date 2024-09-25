package com.hanieum.llmproject.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RetrospectQuestion {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "RETROSPECT_ID")
    private Retrospect retrospect;

    private String topic;
    private String keepContent;
    private String problemContent;
    private String tryContent;
}
