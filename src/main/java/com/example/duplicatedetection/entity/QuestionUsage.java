package com.example.duplicatedetection.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "question_usage")
public class QuestionUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_paper_id", nullable = false)
    private QuestionPaper questionPaper;

    @Column(name = "used_at", nullable = false, updatable = false)
    private LocalDateTime usedAt;

    public QuestionUsage() {
    }

    public QuestionUsage(Question question, QuestionPaper questionPaper) {
        this.question = question;
        this.questionPaper = questionPaper;
    }

    @PrePersist
    protected void onCreate() {
        this.usedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public QuestionPaper getQuestionPaper() {
        return questionPaper;
    }

    public void setQuestionPaper(QuestionPaper questionPaper) {
        this.questionPaper = questionPaper;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
}
