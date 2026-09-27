package com.example.duplicatedetection.dto;

import java.util.ArrayList;
import java.util.List;

public class SimilarityResultDto {

    private String queryQuestionText;
    private double highestSimilarity = 0.0;
    private String status = "UNIQUE"; // HIGHLY_SIMILAR, SIMILAR, UNIQUE
    private String statusDisplay = "The question appears to be unique.";
    private String statusColor = "success"; // danger (red), warning (orange), success (green)
    private String explanation = "No significant semantic overlap detected with existing questions.";
    private boolean duplicate = false;
    private String engineName;
    private List<String> extractedTokens = new ArrayList<>();
    private List<MatchedQuestionDto> matches = new ArrayList<>();

    public SimilarityResultDto() {
    }

    public String getQueryQuestionText() {
        return queryQuestionText;
    }

    public void setQueryQuestionText(String queryQuestionText) {
        this.queryQuestionText = queryQuestionText;
    }

    public double getHighestSimilarity() {
        return highestSimilarity;
    }

    public void setHighestSimilarity(double highestSimilarity) {
        this.highestSimilarity = highestSimilarity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public List<String> getExtractedTokens() {
        return extractedTokens;
    }

    public void setExtractedTokens(List<String> extractedTokens) {
        this.extractedTokens = extractedTokens;
    }

    public List<MatchedQuestionDto> getMatches() {
        return matches;
    }

    public void setMatches(List<MatchedQuestionDto> matches) {
        this.matches = matches;
    }
}
