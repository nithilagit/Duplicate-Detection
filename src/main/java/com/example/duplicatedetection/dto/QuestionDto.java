package com.example.duplicatedetection.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class QuestionDto {

    private Long id;

    @NotBlank(message = "Question text is required")
    @Size(min = 5, max = 2000, message = "Question text must be between 5 and 2000 characters")
    private String questionText;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotBlank(message = "Course Outcome is required")
    private String courseOutcome;

    @NotBlank(message = "Bloom's level is required")
    private String bloomLevel;

    @NotNull(message = "Marks is required")
    @Min(value = 1, message = "Marks must be at least 1")
    @Max(value = 30, message = "Marks cannot exceed 30")
    private Integer marks;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    @NotBlank(message = "Question type is required")
    private String questionType;

    private Integer usageCount = 0;
    private String createdByName;
    private String createdAtFormatted;

    public QuestionDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getCreatedAtFormatted() {
        return createdAtFormatted;
    }

    public void setCreatedAtFormatted(String createdAtFormatted) {
        this.createdAtFormatted = createdAtFormatted;
    }
}
