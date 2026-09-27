package com.example.duplicatedetection;

import com.example.duplicatedetection.ai.SemanticVectorEngine;
import com.example.duplicatedetection.ai.TextPreprocessor;
import com.example.duplicatedetection.ai.TextSimilarityService;
import com.example.duplicatedetection.dto.SimilarityResultDto;
import com.example.duplicatedetection.entity.Faculty;
import com.example.duplicatedetection.entity.Question;
import com.example.duplicatedetection.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TextSimilarityServiceTest {

    private TextPreprocessor preprocessor;
    private SemanticVectorEngine vectorEngine;
    private QuestionRepository questionRepository;
    private TextSimilarityService similarityService;

    @BeforeEach
    void setUp() {
        preprocessor = new TextPreprocessor();
        vectorEngine = new SemanticVectorEngine(preprocessor);
        questionRepository = Mockito.mock(QuestionRepository.class);
        similarityService = new TextSimilarityService(vectorEngine, preprocessor, questionRepository);

        ReflectionTestUtils.setField(similarityService, "thresholdHigh", 85.0);
        ReflectionTestUtils.setField(similarityService, "thresholdMedium", 70.0);
        ReflectionTestUtils.setField(similarityService, "maxRecommendations", 5);
    }

    @Test
    @DisplayName("Should detect identical question with 100% similarity")
    void testExactDuplicate() {
        String qText = "Explain the working principle of TCP congestion control.";
        double score = vectorEngine.calculateSimilarity(qText, qText);
        assertEquals(100.0, score, 0.001);
    }

    @Test
    @DisplayName("Should detect high semantic similarity for paraphrased questions")
    void testParaphrasedSemanticSimilarity() {
        String q1 = "Explain the working principle of TCP congestion control.";
        String q2 = "Describe how congestion control is handled in TCP.";

        double score = vectorEngine.calculateSimilarity(q1, q2);
        assertTrue(score >= 75.0, "Expected score >= 75% for TCP congestion control paraphrasing, got: " + score);
    }

    @Test
    @DisplayName("Should return low similarity for completely unrelated questions")
    void testUnrelatedQuestionsLowSimilarity() {
        String q1 = "Explain Dijkstra's shortest path routing algorithm in computer networks.";
        String q2 = "State and explain the four Coffman conditions required for deadlocks.";

        double score = vectorEngine.calculateSimilarity(q1, q2);
        assertTrue(score < 30.0, "Expected score < 30% for unrelated questions, got: " + score);
    }

    @Test
    @DisplayName("Service should rank matches and classify status appropriately")
    void testServiceAnalyzeQuestion() {
        Faculty faculty = new Faculty("F1", "Prof", "prof@test.com", "pass", "CSE", "ROLE_FACULTY");
        Question q1 = new Question("Explain the working principle of TCP congestion control.",
                "Computer Networks", "Unit III", "CO3", "Understand", 10, "Medium", "Descriptive", faculty);
        q1.setId(1L);

        Question q2 = new Question("Compare OSI reference model with TCP/IP protocol suite.",
                "Computer Networks", "Unit I", "CO1", "Analyze", 8, "Medium", "Descriptive", faculty);
        q2.setId(2L);

        when(questionRepository.findBySubject("Computer Networks")).thenReturn(Arrays.asList(q1, q2));

        SimilarityResultDto result = similarityService.analyzeQuestion(
                "Describe how congestion control is handled in TCP.", "Computer Networks", null
        );

        assertNotNull(result);
        assertFalse(result.getMatches().isEmpty());
        assertEquals(1L, result.getMatches().get(0).getId());
        assertTrue(result.getHighestSimilarity() >= 70.0);
    }
}
