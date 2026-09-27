package com.example.duplicatedetection.repository;

import com.example.duplicatedetection.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE " +
           "(:search IS NULL OR LOWER(q.questionText) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:subject IS NULL OR :subject = '' OR q.subject = :subject) AND " +
           "(:unit IS NULL OR :unit = '' OR q.unit = :unit) AND " +
           "(:co IS NULL OR :co = '' OR q.courseOutcome = :co) AND " +
           "(:bloom IS NULL OR :bloom = '' OR q.bloomLevel = :bloom) AND " +
           "(:difficulty IS NULL OR :difficulty = '' OR q.difficulty = :difficulty) AND " +
           "(:marks IS NULL OR q.marks = :marks) AND " +
           "(:questionType IS NULL OR :questionType = '' OR q.questionType = :questionType)")
    Page<Question> findWithFilters(@Param("search") String search,
                                  @Param("subject") String subject,
                                  @Param("unit") String unit,
                                  @Param("co") String co,
                                  @Param("bloom") String bloom,
                                  @Param("difficulty") String difficulty,
                                  @Param("marks") Integer marks,
                                  @Param("questionType") String questionType,
                                  Pageable pageable);

    List<Question> findBySubject(String subject);

    List<Question> findBySubjectAndUnit(String subject, String unit);

    List<Question> findTop10ByOrderByUsageCountDesc();

    List<Question> findTop10ByOrderByUsageCountAsc();

    @Query("SELECT DISTINCT q.subject FROM Question q ORDER BY q.subject ASC")
    List<String> findDistinctSubjects();

    @Query("SELECT DISTINCT q.unit FROM Question q ORDER BY q.unit ASC")
    List<String> findDistinctUnits();

    @Query("SELECT DISTINCT q.courseOutcome FROM Question q ORDER BY q.courseOutcome ASC")
    List<String> findDistinctCourseOutcomes();

    @Query("SELECT DISTINCT q.bloomLevel FROM Question q ORDER BY q.bloomLevel ASC")
    List<String> findDistinctBloomLevels();

    @Query("SELECT q.bloomLevel as label, COUNT(q) as count FROM Question q GROUP BY q.bloomLevel")
    List<Map<String, Object>> countByBloomLevel();

    @Query("SELECT q.difficulty as label, COUNT(q) as count FROM Question q GROUP BY q.difficulty")
    List<Map<String, Object>> countByDifficulty();

    @Query("SELECT q.unit as label, COUNT(q) as count FROM Question q WHERE (:subject IS NULL OR q.subject = :subject) GROUP BY q.unit ORDER BY q.unit ASC")
    List<Map<String, Object>> countByUnit(@Param("subject") String subject);

    @Query("SELECT q.subject as label, COUNT(q) as count FROM Question q GROUP BY q.subject")
    List<Map<String, Object>> countBySubject();
}
