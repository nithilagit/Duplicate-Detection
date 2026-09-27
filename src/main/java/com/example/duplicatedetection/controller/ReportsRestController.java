package com.example.duplicatedetection.controller;

import com.example.duplicatedetection.dto.ApiResponse;
import com.example.duplicatedetection.dto.DuplicateReportDto;
import com.example.duplicatedetection.dto.QuestionDto;
import com.example.duplicatedetection.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportsRestController {

    private final AnalyticsService analyticsService;

    public ReportsRestController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/duplicates")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDuplicateStats() {
        DuplicateReportDto report = analyticsService.getDuplicateReports();
        Map<String, Object> data = new HashMap<>();
        data.put("totalQuestions", report.getTotalQuestions());
        data.put("totalSimilarityChecks", report.getTotalSimilarityChecks());
        data.put("highSimilarityCount", report.getHighSimilarityCount());
        data.put("mediumSimilarityCount", report.getMediumSimilarityCount());
        data.put("uniqueCount", report.getUniqueCount());
        data.put("averageSimilarityScore", report.getAverageSimilarityScore());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/frequent")
    public ResponseEntity<ApiResponse<List<QuestionDto>>> getFrequentlyAsked() {
        DuplicateReportDto report = analyticsService.getDuplicateReports();
        return ResponseEntity.ok(ApiResponse.ok(report.getFrequentlyUsedQuestions()));
    }

    @GetMapping("/unit-wise")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUnitWiseReports() {
        DuplicateReportDto report = analyticsService.getDuplicateReports();
        Map<String, Object> data = new HashMap<>();
        data.put("unitQuestionCounts", report.getUnitQuestionCounts());
        data.put("difficultyCounts", report.getDifficultyCounts());
        data.put("bloomCounts", report.getBloomCounts());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/usage")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUsageAnalysis() {
        DuplicateReportDto report = analyticsService.getDuplicateReports();
        Map<String, Object> data = new HashMap<>();
        data.put("frequentlyUsed", report.getFrequentlyUsedQuestions());
        data.put("leastUsed", report.getLeastUsedQuestions());
        data.put("subjectUsage", report.getSubjectUsage());
        data.put("unitUsage", report.getUnitUsage());
        return ResponseEntity.ok(ApiResponse.ok(data));
    }
}
