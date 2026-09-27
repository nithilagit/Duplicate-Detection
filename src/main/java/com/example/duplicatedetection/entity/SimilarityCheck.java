package com.example.duplicatedetection.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "similarity_checks", indexes = {
    @Index(name = "idx_sim_status", columnList = "status"),
    @Index(name = "idx_sim_checked_at", columnList = "checked_at")
})
public class SimilarityCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "new_question_text", nullable = false, columnDefinition = "TEXT")
    private String newQuestionText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matched_question_id")
    private Question matchedQuestion;

    @Column(name = "matched_question_text", columnDefinition = "TEXT")
    private String matchedQuestionText;

    @Column(name = "similarity_score", nullable = false)
    private Double similarityScore;

    @Column(nullable = false, length = 50)
    private String status; // HIGHLY_SIMILAR, SIMILAR, UNIQUE

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @Column(name = "checked_at", nullable = false, updatable = false)
    private LocalDateTime checkedAt;

    public SimilarityCheck() {
    }

    public SimilarityCheck(String newQuestionText, Question matchedQuestion, String matchedQuestionText,
                           Double similarityScore, String status, String recommendation, Faculty faculty) {
        this.newQuestionText = newQuestionText;
        this.matchedQuestion = matchedQuestion;
        this.matchedQuestionText = matchedQuestionText;
        this.similarityScore = similarityScore;
        this.status = status;
        this.recommendation = recommendation;
        this.faculty = faculty;
    }

    @PrePersist
    protected void onCreate() {
        this.checkedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNewQuestionText() {
        return newQuestionText;
    }

    public void setNewQuestionText(String newQuestionText) {
        this.newQuestionText = newQuestionText;
    }

    public Question getMatchedQuestion() {
        return matchedQuestion;
    }

    public void setMatchedQuestion(Question matchedQuestion) {
        this.matchedQuestion = matchedQuestion;
    }

    public String getMatchedQuestionText() {
        return matchedQuestionText;
    }

    public void setMatchedQuestionText(String matchedQuestionText) {
        this.matchedQuestionText = matchedQuestionText;
    }

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
    }
}
