package com.example.duplicatedetection.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DuplicateReportDto {

    private long totalQuestions;
    private long totalSimilarityChecks;
    private long highSimilarityCount;
    private long mediumSimilarityCount;
    private long uniqueCount;
    private double averageSimilarityScore;

    private List<QuestionDto> frequentlyUsedQuestions = new ArrayList<>();
    private List<QuestionDto> leastUsedQuestions = new ArrayList<>();

    private Map<String, Long> subjectUsage = new LinkedHashMap<>();
    private Map<String, Long> unitUsage = new LinkedHashMap<>();
    private Map<String, Long> unitQuestionCounts = new LinkedHashMap<>();
    private Map<String, Long> difficultyCounts = new LinkedHashMap<>();
    private Map<String, Long> bloomCounts = new LinkedHashMap<>();

    public DuplicateReportDto() {
    }

    public long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public long getTotalSimilarityChecks() {
        return totalSimilarityChecks;
    }

    public void setTotalSimilarityChecks(long totalSimilarityChecks) {
        this.totalSimilarityChecks = totalSimilarityChecks;
    }

    public long getHighSimilarityCount() {
        return highSimilarityCount;
    }

    public void setHighSimilarityCount(long highSimilarityCount) {
        this.highSimilarityCount = highSimilarityCount;
    }

    public long getMediumSimilarityCount() {
        return mediumSimilarityCount;
    }

    public void setMediumSimilarityCount(long mediumSimilarityCount) {
        this.mediumSimilarityCount = mediumSimilarityCount;
    }

    public long getUniqueCount() {
        return uniqueCount;
    }

    public void setUniqueCount(long uniqueCount) {
        this.uniqueCount = uniqueCount;
    }

    public double getAverageSimilarityScore() {
        return averageSimilarityScore;
    }

    public void setAverageSimilarityScore(double averageSimilarityScore) {
        this.averageSimilarityScore = averageSimilarityScore;
    }

    public List<QuestionDto> getFrequentlyUsedQuestions() {
        return frequentlyUsedQuestions;
    }

    public void setFrequentlyUsedQuestions(List<QuestionDto> frequentlyUsedQuestions) {
        this.frequentlyUsedQuestions = frequentlyUsedQuestions;
    }

    public List<QuestionDto> getLeastUsedQuestions() {
        return leastUsedQuestions;
    }

    public void setLeastUsedQuestions(List<QuestionDto> leastUsedQuestions) {
        this.leastUsedQuestions = leastUsedQuestions;
    }

    public Map<String, Long> getSubjectUsage() {
        return subjectUsage;
    }

    public void setSubjectUsage(Map<String, Long> subjectUsage) {
        this.subjectUsage = subjectUsage;
    }

    public Map<String, Long> getUnitUsage() {
        return unitUsage;
    }

    public void setUnitUsage(Map<String, Long> unitUsage) {
        this.unitUsage = unitUsage;
    }

    public Map<String, Long> getUnitQuestionCounts() {
        return unitQuestionCounts;
    }

    public void setUnitQuestionCounts(Map<String, Long> unitQuestionCounts) {
        this.unitQuestionCounts = unitQuestionCounts;
    }

    public Map<String, Long> getDifficultyCounts() {
        return difficultyCounts;
    }

    public void setDifficultyCounts(Map<String, Long> difficultyCounts) {
        this.difficultyCounts = difficultyCounts;
    }

    public Map<String, Long> getBloomCounts() {
        return bloomCounts;
    }

    public void setBloomCounts(Map<String, Long> bloomCounts) {
        this.bloomCounts = bloomCounts;
    }
}
