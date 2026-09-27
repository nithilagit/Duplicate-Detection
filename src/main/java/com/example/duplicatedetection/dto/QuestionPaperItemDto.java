package com.example.duplicatedetection.dto;

import java.util.ArrayList;
import java.util.List;

public class QuestionPaperItemDto {
    private Long questionId;
    private Integer questionNumber;
    private String sectionName = "Part A";
    private Integer allocatedMarks;
    private String questionText;
    private String unit;
    private String courseOutcome;
    private String bloomLevel;
    private String difficulty;

    public QuestionPaperItemDto() {
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getAllocatedMarks() {
        return allocatedMarks;
    }

    public void setAllocatedMarks(Integer allocatedMarks) {
        this.allocatedMarks = allocatedMarks;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCourseOutcome() {
        return courseOutcome;
    }

    public void setCourseOutcome(String courseOutcome) {
        this.courseOutcome = courseOutcome;
    }

    public String getBloomLevel() {
        return bloomLevel;
    }

    public void setBloomLevel(String bloomLevel) {
        this.bloomLevel = bloomLevel;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
