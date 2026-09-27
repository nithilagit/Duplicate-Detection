package com.example.duplicatedetection.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question_paper_questions")
public class QuestionPaperQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_paper_id", nullable = false)
    private QuestionPaper questionPaper;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "question_number", nullable = false)
    private Integer questionNumber;

    @Column(name = "section_name", nullable = false, length = 50)
    private String sectionName = "Part A";

    @Column(name = "allocated_marks", nullable = false)
    private Integer allocatedMarks;

    public QuestionPaperQuestion() {
    }

    public QuestionPaperQuestion(QuestionPaper questionPaper, Question question, Integer questionNumber,
                                 String sectionName, Integer allocatedMarks) {
        this.questionPaper = questionPaper;
        this.question = question;
        this.questionNumber = questionNumber;
        this.sectionName = sectionName != null ? sectionName : "Part A";
        this.allocatedMarks = allocatedMarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuestionPaper getQuestionPaper() {
        return questionPaper;
    }

    public void setQuestionPaper(QuestionPaper questionPaper) {
        this.questionPaper = questionPaper;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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
}
