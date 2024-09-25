package com.hanieum.llmproject.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RetrospectQuestion {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "RETROSPECT_ID")
    private Retrospect retrospect;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] topic;
    private String keepContent;
    private String problemContent;
    private String tryContent;
    private LocalDateTime createdAt;

    public RetrospectQuestion(
            Retrospect retrospect,
            String topic,
            String keepContent,
            String problemContent,
            String tryContent) {
        this.retrospect = retrospect;
        this.topic = topic.getBytes();
        this.keepContent = keepContent;
        this.problemContent = problemContent;
        this.tryContent = tryContent;
        this.createdAt = LocalDateTime.now();
    }
}
