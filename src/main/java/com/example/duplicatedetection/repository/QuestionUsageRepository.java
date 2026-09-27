package com.example.duplicatedetection.repository;

import com.example.duplicatedetection.entity.QuestionUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionUsageRepository extends JpaRepository<QuestionUsage, Long> {
    List<QuestionUsage> findByQuestionId(Long questionId);
    List<QuestionUsage> findTop20ByOrderByUsedAtDesc();
}
