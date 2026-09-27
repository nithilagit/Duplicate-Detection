package com.example.duplicatedetection;

import com.example.duplicatedetection.ai.SemanticVectorEngine;
import com.example.duplicatedetection.dto.QuestionDto;
import com.example.duplicatedetection.entity.Faculty;
import com.example.duplicatedetection.entity.Question;
import com.example.duplicatedetection.repository.QuestionEmbeddingRepository;
import com.example.duplicatedetection.repository.QuestionRepository;
import com.example.duplicatedetection.service.ActivityLogService;
import com.example.duplicatedetection.service.QuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuestionServiceTest {

    private QuestionRepository questionRepository;
    private QuestionEmbeddingRepository embeddingRepository;
    private SemanticVectorEngine vectorEngine;
    private ActivityLogService activityLogService;
    private ObjectMapper objectMapper;
    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        questionRepository = Mockito.mock(QuestionRepository.class);
        embeddingRepository = Mockito.mock(QuestionEmbeddingRepository.class);
        vectorEngine = Mockito.mock(SemanticVectorEngine.class);
        activityLogService = Mockito.mock(ActivityLogService.class);
        objectMapper = new ObjectMapper();

        questionService = new QuestionService(
                questionRepository,
                embeddingRepository,
                vectorEngine,
                activityLogService,
                objectMapper
        );
    }

    @Test
    @DisplayName("Should create question and generate embedding")
    void testCreateQuestion() {
        Faculty faculty = new Faculty("F-101", "Dr. Jenkins", "faculty@eec.srmrmp.edu.in", "pass", "Artificial Intelligence and Data Science", "ROLE_FACULTY");

        QuestionDto inputDto = new QuestionDto();
        inputDto.setQuestionText("Explain TCP three-way handshake.");
        inputDto.setSubject("Computer Networks");
        inputDto.setUnit("Unit III");
        inputDto.setCourseOutcome("CO3");
        inputDto.setBloomLevel("Understand");
        inputDto.setMarks(10);
        inputDto.setDifficulty("Medium");
        inputDto.setQuestionType("Descriptive");

        Question saved = new Question("Explain TCP three-way handshake.", "Computer Networks", "Unit III", "CO3",
                "Understand", 10, "Medium", "Descriptive", faculty);
        saved.setId(100L);

        when(questionRepository.save(any(Question.class))).thenReturn(saved);

        QuestionDto result = questionService.createQuestion(inputDto, faculty);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Explain TCP three-way handshake.", result.getQuestionText());
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    @DisplayName("Should return questions page with filter criteria")
    void testGetQuestions() {
        Question q = new Question("Test Q", "Sub", "Unit I", "CO1", "Apply", 5, "Easy", "MCQ", null);
        q.setId(5L);
        Page<Question> mockPage = new PageImpl<>(Collections.singletonList(q));

        when(questionRepository.findWithFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<QuestionDto> page = questionService.getQuestions(
                null, "Sub", "Unit I", null, null, null, null, null, 0, 10, "id", "desc"
        );

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        assertEquals(5L, page.getContent().get(0).getId());
    }
}
