package com.example.duplicatedetection.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardStatsDto {

    private long totalQuestions = 0;
    private long totalDuplicatesDetected = 0;
    private long totalUniqueQuestions = 0;
    private long totalQuestionPapers = 0;
    private String mostUsedQuestion = "None yet";
    private int mostUsedQuestionCount = 0;
    private double averageSimilarity = 0.0;

    private Map<String, Long> unitDistribution = new LinkedHashMap<>();
    private Map<String, Long> bloomDistribution = new LinkedHashMap<>();
    private Map<String, Long> difficultyDistribution = new LinkedHashMap<>();
    private Map<String, Long> duplicateStatusDistribution = new LinkedHashMap<>();

    private List<QuestionDto> recentQuestions = new ArrayList<>();
    private List<SimilarityResultDto> recentChecks = new ArrayList<>();
    private List<QuestionPaperDto> recentPapers = new ArrayList<>();
    private List<String> recentActivityMessages = new ArrayList<>();

    public DashboardStatsDto() {
    }

    public long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public long getTotalDuplicatesDetected() {
        return totalDuplicatesDetected;
    }

    public void setTotalDuplicatesDetected(long totalDuplicatesDetected) {
        this.totalDuplicatesDetected = totalDuplicatesDetected;
    }

    public long getTotalUniqueQuestions() {
        return totalUniqueQuestions;
    }

    public void setTotalUniqueQuestions(long totalUniqueQuestions) {
        this.totalUniqueQuestions = totalUniqueQuestions;
    }

    public long getTotalQuestionPapers() {
        return totalQuestionPapers;
    }

    public void setTotalQuestionPapers(long totalQuestionPapers) {
        this.totalQuestionPapers = totalQuestionPapers;
    }

    public String getMostUsedQuestion() {
        return mostUsedQuestion;
    }

    public void setMostUsedQuestion(String mostUsedQuestion) {
        this.mostUsedQuestion = mostUsedQuestion;
    }

    public int getMostUsedQuestionCount() {
        return mostUsedQuestionCount;
    }

    public void setMostUsedQuestionCount(int mostUsedQuestionCount) {
        this.mostUsedQuestionCount = mostUsedQuestionCount;
    }

    public double getAverageSimilarity() {
        return averageSimilarity;
    }

    public void setAverageSimilarity(double averageSimilarity) {
        this.averageSimilarity = averageSimilarity;
    }

    public Map<String, Long> getUnitDistribution() {
        return unitDistribution;
    }

    public void setUnitDistribution(Map<String, Long> unitDistribution) {
        this.unitDistribution = unitDistribution;
    }

    public Map<String, Long> getBloomDistribution() {
        return bloomDistribution;
    }

    public void setBloomDistribution(Map<String, Long> bloomDistribution) {
        this.bloomDistribution = bloomDistribution;
    }

    public Map<String, Long> getDifficultyDistribution() {
        return difficultyDistribution;
    }

    public void setDifficultyDistribution(Map<String, Long> difficultyDistribution) {
        this.difficultyDistribution = difficultyDistribution;
    }

    public Map<String, Long> getDuplicateStatusDistribution() {
        return duplicateStatusDistribution;
    }

    public void setDuplicateStatusDistribution(Map<String, Long> duplicateStatusDistribution) {
        this.duplicateStatusDistribution = duplicateStatusDistribution;
    }

    public List<QuestionDto> getRecentQuestions() {
        return recentQuestions;
    }

    public void setRecentQuestions(List<QuestionDto> recentQuestions) {
        this.recentQuestions = recentQuestions;
    }

    public List<SimilarityResultDto> getRecentChecks() {
        return recentChecks;
    }

    public void setRecentChecks(List<SimilarityResultDto> recentChecks) {
        this.recentChecks = recentChecks;
    }

    public List<QuestionPaperDto> getRecentPapers() {
        return recentPapers;
    }

    public void setRecentPapers(List<QuestionPaperDto> recentPapers) {
        this.recentPapers = recentPapers;
    }

    public List<String> getRecentActivityMessages() {
        return recentActivityMessages;
    }

    public void setRecentActivityMessages(List<String> recentActivityMessages) {
        this.recentActivityMessages = recentActivityMessages;
    }
}
