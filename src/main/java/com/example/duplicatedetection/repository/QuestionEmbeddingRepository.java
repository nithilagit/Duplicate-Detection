package com.example.duplicatedetection.repository;

import com.example.duplicatedetection.entity.Question;
import com.example.duplicatedetection.entity.QuestionEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionEmbeddingRepository extends JpaRepository<QuestionEmbedding, Long> {
    Optional<QuestionEmbedding> findByQuestion(Question question);
    Optional<QuestionEmbedding> findByQuestionId(Long questionId);
    void deleteByQuestionId(Long questionId);
}
