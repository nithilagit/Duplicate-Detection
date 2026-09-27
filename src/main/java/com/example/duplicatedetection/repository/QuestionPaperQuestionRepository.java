package com.example.duplicatedetection.repository;

import com.example.duplicatedetection.entity.QuestionPaperQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionPaperQuestionRepository extends JpaRepository<QuestionPaperQuestion, Long> {
    List<QuestionPaperQuestion> findByQuestionPaperIdOrderByQuestionNumberAsc(Long questionPaperId);
    void deleteByQuestionPaperId(Long questionPaperId);
}
