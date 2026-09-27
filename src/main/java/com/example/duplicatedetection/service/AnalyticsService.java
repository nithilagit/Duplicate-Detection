package com.example.duplicatedetection.service;

import com.example.duplicatedetection.dto.DashboardStatsDto;
import com.example.duplicatedetection.dto.DuplicateReportDto;
import com.example.duplicatedetection.dto.QuestionDto;
import com.example.duplicatedetection.entity.ActivityLog;
import com.example.duplicatedetection.entity.Question;
import com.example.duplicatedetection.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final QuestionRepository questionRepository;
    private final SimilarityCheckRepository similarityCheckRepository;
    private final QuestionPaperRepository paperRepository;
    private final ActivityLogRepository activityLogRepository;
    private final QuestionService questionService;

    public AnalyticsService(QuestionRepository questionRepository,
                            SimilarityCheckRepository similarityCheckRepository,
                            QuestionPaperRepository paperRepository,
                            ActivityLogRepository activityLogRepository,
                            QuestionService questionService) {
        this.questionRepository = questionRepository;
        this.similarityCheckRepository = similarityCheckRepository;
        this.paperRepository = paperRepository;
        this.activityLogRepository = activityLogRepository;
        this.questionService = questionService;
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        long totalQuestions = questionRepository.count();
        stats.setTotalQuestions(totalQuestions);

        long highSimilar = similarityCheckRepository.countByStatus("HIGHLY_SIMILAR");
        long mediumSimilar = similarityCheckRepository.countByStatus("SIMILAR");
        long uniqueChecks = similarityCheckRepository.countByStatus("UNIQUE");
        long totalChecks = highSimilar + mediumSimilar + uniqueChecks;

        stats.setTotalDuplicatesDetected(highSimilar + mediumSimilar);
        stats.setTotalUniqueQuestions(uniqueChecks);
        stats.setTotalQuestionPapers(paperRepository.count());

        Double avgSim = similarityCheckRepository.getAverageSimilarityScore();
        stats.setAverageSimilarity(avgSim != null ? Math.round(avgSim * 10.0) / 10.0 : 0.0);

        // Most used question
        List<Question> topUsed = questionRepository.findTop10ByOrderByUsageCountDesc();
        if (!topUsed.isEmpty() && topUsed.get(0).getUsageCount() > 0) {
            stats.setMostUsedQuestion(topUsed.get(0).getQuestionText());
            stats.setMostUsedQuestionCount(topUsed.get(0).getUsageCount());
        }

        // Bloom's taxonomy distribution
        Map<String, Long> bloomMap = new LinkedHashMap<>();
        for (String bloom : Arrays.asList("Remember", "Understand", "Apply", "Analyze", "Evaluate", "Create")) {
            bloomMap.put(bloom, 0L);
        }
        for (Map<String, Object> row : questionRepository.countByBloomLevel()) {
            if (row.get("label") != null) {
                bloomMap.put((String) row.get("label"), ((Number) row.get("count")).longValue());
            }
        }
        stats.setBloomDistribution(bloomMap);

        // Difficulty distribution
        Map<String, Long> diffMap = new LinkedHashMap<>();
        diffMap.put("Easy", 0L);
        diffMap.put("Medium", 0L);
        diffMap.put("Hard", 0L);
        for (Map<String, Object> row : questionRepository.countByDifficulty()) {
            if (row.get("label") != null) {
                diffMap.put((String) row.get("label"), ((Number) row.get("count")).longValue());
            }
        }
        stats.setDifficultyDistribution(diffMap);

        // Unit distribution
        Map<String, Long> unitMap = new LinkedHashMap<>();
        for (Map<String, Object> row : questionRepository.countByUnit(null)) {
            if (row.get("label") != null) {
                unitMap.put((String) row.get("label"), ((Number) row.get("count")).longValue());
            }
        }
        stats.setUnitDistribution(unitMap);

        // Duplicate status distribution
        Map<String, Long> dupMap = new LinkedHashMap<>();
        dupMap.put("Highly Similar", highSimilar);
        dupMap.put("Review Required", mediumSimilar);
        dupMap.put("Unique Questions", uniqueChecks);
        stats.setDuplicateStatusDistribution(dupMap);

        // Recent activity
        List<ActivityLog> logs = activityLogRepository.findTop15ByOrderByCreatedAtDesc();
        stats.setRecentActivityMessages(logs.stream()
                .map(l -> String.format("[%s] %s: %s",
                        l.getAction(),
                        l.getFaculty() != null ? l.getFaculty().getName() : "System",
                        l.getDetails()))
                .collect(Collectors.toList()));

        // Recent questions
        stats.setRecentQuestions(questionRepository.findAll().stream()
                .sorted(Comparator.comparing(Question::getId).reversed())
                .limit(5)
                .map(questionService::toDto)
                .collect(Collectors.toList()));

        return stats;
    }

    public DuplicateReportDto getDuplicateReports() {
        DuplicateReportDto report = new DuplicateReportDto();

        report.setTotalQuestions(questionRepository.count());
        long high = similarityCheckRepository.countByStatus("HIGHLY_SIMILAR");
        long medium = similarityCheckRepository.countByStatus("SIMILAR");
        long unique = similarityCheckRepository.countByStatus("UNIQUE");

        report.setHighSimilarityCount(high);
        report.setMediumSimilarityCount(medium);
        report.setUniqueCount(unique);
        report.setTotalSimilarityChecks(high + medium + unique);

        Double avgSim = similarityCheckRepository.getAverageSimilarityScore();
        report.setAverageSimilarityScore(avgSim != null ? Math.round(avgSim * 10.0) / 10.0 : 0.0);

        // Frequently used questions
        report.setFrequentlyUsedQuestions(questionRepository.findTop10ByOrderByUsageCountDesc().stream()
                .map(questionService::toDto)
                .collect(Collectors.toList()));

        // Least used questions
        report.setLeastUsedQuestions(questionRepository.findTop10ByOrderByUsageCountAsc().stream()
                .map(questionService::toDto)
                .collect(Collectors.toList()));

        // Unit-wise count
        Map<String, Long> unitCounts = new LinkedHashMap<>();
        for (Map<String, Object> row : questionRepository.countByUnit(null)) {
            if (row.get("label") != null) {
                unitCounts.put((String) row.get("label"), ((Number) row.get("count")).longValue());
            }
        }
        report.setUnitQuestionCounts(unitCounts);

        // Difficulty counts
        Map<String, Long> diffCounts = new LinkedHashMap<>();
        for (Map<String, Object> row : questionRepository.countByDifficulty()) {
            if (row.get("label") != null) {
                diffCounts.put((String) row.get("label"), ((Number) row.get("count")).longValue());
            }
        }
        report.setDifficultyCounts(diffCounts);

        // Bloom counts
        Map<String, Long> bloomCounts = new LinkedHashMap<>();
        for (Map<String, Object> row : questionRepository.countByBloomLevel()) {
            if (row.get("label") != null) {
                bloomCounts.put((String) row.get("label"), ((Number) row.get("count")).longValue());
            }
        }
        report.setBloomCounts(bloomCounts);

        return report;
    }
}
