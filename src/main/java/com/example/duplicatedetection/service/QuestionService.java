package com.example.duplicatedetection.service;

import com.example.duplicatedetection.ai.SemanticVectorEngine;
import com.example.duplicatedetection.dto.QuestionDto;
import com.example.duplicatedetection.entity.Faculty;
import com.example.duplicatedetection.entity.Question;
import com.example.duplicatedetection.entity.QuestionEmbedding;
import com.example.duplicatedetection.exception.ResourceNotFoundException;
import com.example.duplicatedetection.repository.QuestionEmbeddingRepository;
import com.example.duplicatedetection.repository.QuestionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionEmbeddingRepository embeddingRepository;
    private final SemanticVectorEngine vectorEngine;
    private final ActivityLogService activityLogService;
    private final ObjectMapper objectMapper;

    @Value("${app.curriculum.subjects:Discrete Mathematics,Computer Networks,Advanced Data Structures and Algorithms,Embedded System Design,Machine Learning Techniques,Object Oriented Programming using Java}")
    private String configuredSubjects;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public QuestionService(QuestionRepository questionRepository,
                           QuestionEmbeddingRepository embeddingRepository,
                           SemanticVectorEngine vectorEngine,
                           ActivityLogService activityLogService,
                           ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.embeddingRepository = embeddingRepository;
        this.vectorEngine = vectorEngine;
        this.activityLogService = activityLogService;
        this.objectMapper = objectMapper;
    }

    public Page<QuestionDto> getQuestions(String search, String subject, String unit, String co,
                                          String bloom, String difficulty, Integer marks,
                                          String questionType, int page, int size, String sortBy, String direction) {
        Sort sort = Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC,
                (sortBy == null || sortBy.isEmpty()) ? "id" : sortBy);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        Page<Question> questions = questionRepository.findWithFilters(cleanSearch, subject, unit, co, bloom, difficulty, marks, questionType, pageable);
        return questions.map(this::toDto);
    }

    public List<QuestionDto> getAllQuestions() {
        return questionRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<QuestionDto> getQuestionsBySubject(String subject) {
        return questionRepository.findBySubject(subject).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Question getQuestionEntity(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + id));
    }

    public QuestionDto getQuestionById(Long id) {
        return toDto(getQuestionEntity(id));
    }

    @Transactional
    public QuestionDto createQuestion(QuestionDto dto, Faculty faculty) {
        Question question = new Question();
        question.setQuestionText(dto.getQuestionText().trim());
        question.setSubject(dto.getSubject());
        question.setUnit(dto.getUnit());
        question.setCourseOutcome(dto.getCourseOutcome());
        question.setBloomLevel(dto.getBloomLevel());
        question.setMarks(dto.getMarks());
        question.setDifficulty(dto.getDifficulty());
        question.setQuestionType(dto.getQuestionType());
        question.setCreatedBy(faculty);
        question.setUsageCount(0);

        Question saved = questionRepository.save(question);

        // Generate and persist vector embedding
        saveEmbedding(saved);

        activityLogService.logActivity(faculty, "ADDED_QUESTION",
                String.format("Added question #%d for %s (Unit %s)", saved.getId(), saved.getSubject(), saved.getUnit()));

        return toDto(saved);
    }

    @Transactional
    public QuestionDto updateQuestion(Long id, QuestionDto dto, Faculty faculty) {
        Question question = getQuestionEntity(id);
        question.setQuestionText(dto.getQuestionText().trim());
        question.setSubject(dto.getSubject());
        question.setUnit(dto.getUnit());
        question.setCourseOutcome(dto.getCourseOutcome());
        question.setBloomLevel(dto.getBloomLevel());
        question.setMarks(dto.getMarks());
        question.setDifficulty(dto.getDifficulty());
        question.setQuestionType(dto.getQuestionType());

        Question updated = questionRepository.save(question);

        // Update embedding
        saveEmbedding(updated);

        activityLogService.logActivity(faculty, "UPDATED_QUESTION",
                String.format("Updated question #%d: %s", updated.getId(), updated.getSubject()));

        return toDto(updated);
    }

    @Transactional
    public void deleteQuestion(Long id, Faculty faculty) {
        Question question = getQuestionEntity(id);
        embeddingRepository.deleteByQuestionId(id);
        questionRepository.delete(question);

        activityLogService.logActivity(faculty, "DELETED_QUESTION",
                String.format("Deleted question #%d (%s)", id, question.getSubject()));
    }

    private void saveEmbedding(Question question) {
        try {
            Map<String, Double> vector = vectorEngine.generateEmbeddingVector(question.getQuestionText());
            String json = objectMapper.writeValueAsString(vector);

            QuestionEmbedding embedding = embeddingRepository.findByQuestion(question)
                    .orElse(new QuestionEmbedding(question, json, vectorEngine.getEngineName()));

            embedding.setEmbeddingJson(json);
            embedding.setModelVersion(vectorEngine.getEngineName());
            embeddingRepository.save(embedding);
        } catch (JsonProcessingException e) {
            // Non-fatal fallback; embedding can be re-computed on demand
        }
    }

    public List<String> getSubjects() {
        List<String> list = new ArrayList<>();
        if (configuredSubjects != null && !configuredSubjects.trim().isEmpty()) {
            for (String s : configuredSubjects.split(",")) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty() && !list.contains(trimmed)) {
                    list.add(trimmed);
                }
            }
        }
        List<String> dbSubjects = questionRepository.findDistinctSubjects();
        for (String s : dbSubjects) {
            if (s != null && !s.trim().isEmpty() && !list.contains(s.trim())) {
                list.add(s.trim());
            }
        }
        return list;
    }

    public List<String> getUnits() {
        return questionRepository.findDistinctUnits();
    }

    public List<String> getCourseOutcomes() {
        return questionRepository.findDistinctCourseOutcomes();
    }

    public List<String> getBloomLevels() {
        return questionRepository.findDistinctBloomLevels();
    }

    public QuestionDto toDto(Question q) {
        QuestionDto dto = new QuestionDto();
        dto.setId(q.getId());
        dto.setQuestionText(q.getQuestionText());
        dto.setSubject(q.getSubject());
        dto.setUnit(q.getUnit());
        dto.setCourseOutcome(q.getCourseOutcome());
        dto.setBloomLevel(q.getBloomLevel());
        dto.setMarks(q.getMarks());
        dto.setDifficulty(q.getDifficulty());
        dto.setQuestionType(q.getQuestionType());
        dto.setUsageCount(q.getUsageCount() != null ? q.getUsageCount() : 0);

        if (q.getCreatedBy() != null) {
            dto.setCreatedByName(q.getCreatedBy().getName());
        } else {
            dto.setCreatedByName("System Admin");
        }

        if (q.getCreatedAt() != null) {
            dto.setCreatedAtFormatted(q.getCreatedAt().format(DATE_FORMATTER));
        }
        return dto;
    }
}
