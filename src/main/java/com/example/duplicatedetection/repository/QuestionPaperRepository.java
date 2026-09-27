package com.example.duplicatedetection.repository;

import com.example.duplicatedetection.entity.QuestionPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionPaperRepository extends JpaRepository<QuestionPaper, Long> {
    List<QuestionPaper> findTop10ByOrderByCreatedAtDesc();
    List<QuestionPaper> findBySubject(String subject);
}
