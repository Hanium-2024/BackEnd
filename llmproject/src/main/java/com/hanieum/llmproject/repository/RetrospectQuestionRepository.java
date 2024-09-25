package com.hanieum.llmproject.repository;

import com.hanieum.llmproject.model.Retrospect;
import com.hanieum.llmproject.model.RetrospectQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RetrospectQuestionRepository extends JpaRepository<RetrospectQuestion, Long> {
    List<RetrospectQuestion> findAllByRetrospect(Retrospect retrospect);
}
