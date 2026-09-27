package com.example.duplicatedetection.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions", indexes = {
    @Index(name = "idx_q_subject", columnList = "subject"),
    @Index(name = "idx_q_unit", columnList = "unit"),
    @Index(name = "idx_q_co", columnList = "course_outcome"),
    @Index(name = "idx_q_bloom", columnList = "bloom_level"),
    @Index(name = "idx_q_diff", columnList = "difficulty"),
    @Index(name = "idx_q_type", columnList = "question_type")
})
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(nullable = false, length = 100)
    private String subject;

    @Column(nullable = false, length = 50)
    private String unit;

    @Column(name = "course_outcome", nullable = false, length = 50)
    private String courseOutcome;

    @Column(name = "bloom_level", nullable = false, length = 50)
    private String bloomLevel;

    @Column(nullable = false)
    private Integer marks;

    @Column(nullable = false, length = 50)
    private String difficulty;

    @Column(name = "question_type", nullable = false, length = 50)
    private String questionType;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private Faculty createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Question() {
    }

    public Question(String questionText, String subject, String unit, String courseOutcome,
                    String bloomLevel, Integer marks, String difficulty, String questionType, Faculty createdBy) {
        this.questionText = questionText;
        this.subject = subject;
        this.unit = unit;
        this.courseOutcome = courseOutcome;
        this.bloomLevel = bloomLevel;
        this.marks = marks;
        this.difficulty = difficulty;
        this.questionType = questionType;
        this.createdBy = createdBy;
        this.usageCount = 0;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.usageCount == null) {
            this.usageCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public Faculty getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Faculty createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
