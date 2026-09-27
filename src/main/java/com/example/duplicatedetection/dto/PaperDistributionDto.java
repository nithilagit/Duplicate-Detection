package com.example.duplicatedetection.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaperDistributionDto {

    private int totalQuestions = 0;
    private int totalMarks = 0;
    private Map<String, Integer> unitDistribution = new LinkedHashMap<>();
    private Map<String, Double> coPercentages = new LinkedHashMap<>();
    private Map<String, Integer> bloomDistribution = new LinkedHashMap<>();
    private Map<String, Integer> difficultyDistribution = new LinkedHashMap<>();
    private String averageDifficulty = "Balanced";
    private boolean isBalanced = true;
    private List<String> advisoryWarnings = new ArrayList<>();

    public PaperDistributionDto() {
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Map<String, Integer> getUnitDistribution() {
        return unitDistribution;
    }

    public void setUnitDistribution(Map<String, Integer> unitDistribution) {
        this.unitDistribution = unitDistribution;
    }

    public Map<String, Double> getCoPercentages() {
        return coPercentages;
    }

    public void setCoPercentages(Map<String, Double> coPercentages) {
        this.coPercentages = coPercentages;
    }

    public Map<String, Integer> getBloomDistribution() {
        return bloomDistribution;
    }

    public void setBloomDistribution(Map<String, Integer> bloomDistribution) {
        this.bloomDistribution = bloomDistribution;
    }

    public Map<String, Integer> getDifficultyDistribution() {
        return difficultyDistribution;
    }

    public void setDifficultyDistribution(Map<String, Integer> difficultyDistribution) {
        this.difficultyDistribution = difficultyDistribution;
    }

    public String getAverageDifficulty() {
        return averageDifficulty;
    }

    public void setAverageDifficulty(String averageDifficulty) {
        this.averageDifficulty = averageDifficulty;
    }

    public boolean isBalanced() {
        return isBalanced;
    }

    public void setBalanced(boolean balanced) {
        isBalanced = balanced;
    }

    public List<String> getAdvisoryWarnings() {
        return advisoryWarnings;
    }

    public void setAdvisoryWarnings(List<String> advisoryWarnings) {
        this.advisoryWarnings = advisoryWarnings;
    }
}
