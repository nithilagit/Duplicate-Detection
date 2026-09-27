package com.example.duplicatedetection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SimilarityCheckRequest {

    @NotBlank(message = "Question text cannot be empty")
    @Size(min = 5, max = 2000, message = "Question text must be between 5 and 2000 characters")
    private String questionText;

    private String subject;
    private Long excludeQuestionId; // Useful when editing an existing question so it doesn't match itself

    public SimilarityCheckRequest() {
    }

    public SimilarityCheckRequest(String questionText, String subject) {
        this.questionText = questionText;
        this.subject = subject;
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

    public Long getExcludeQuestionId() {
        return excludeQuestionId;
    }

    public void setExcludeQuestionId(Long excludeQuestionId) {
        this.excludeQuestionId = excludeQuestionId;
    }
}
