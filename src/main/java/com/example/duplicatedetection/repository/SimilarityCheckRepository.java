package com.example.duplicatedetection.repository;

import com.example.duplicatedetection.entity.SimilarityCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SimilarityCheckRepository extends JpaRepository<SimilarityCheck, Long> {

    List<SimilarityCheck> findTop10ByOrderByCheckedAtDesc();

    long countByStatus(String status);

    @Query("SELECT AVG(s.similarityScore) FROM SimilarityCheck s")
    Double getAverageSimilarityScore();

    @Query("SELECT s.status as label, COUNT(s) as count FROM SimilarityCheck s GROUP BY s.status")
    List<Map<String, Object>> countGroupedByStatus();
}
