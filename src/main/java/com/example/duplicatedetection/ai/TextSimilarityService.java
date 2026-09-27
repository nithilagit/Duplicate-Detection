package com.example.duplicatedetection.ai;

import com.example.duplicatedetection.dto.MatchedQuestionDto;
import com.example.duplicatedetection.dto.SimilarityResultDto;
import com.example.duplicatedetection.entity.Question;
import com.example.duplicatedetection.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * High-level AI Similarity Service for comparing questions against the institutional question bank.
 * Uses configurable thresholds and produces structured recommendations with visual indicator metadata.
 */
@Service
public class TextSimilarityService {

    private final SimilarityEngine similarityEngine;
    private final TextPreprocessor preprocessor;
    private final QuestionRepository questionRepository;

    @Value("${app.similarity.threshold.high:85.0}")
    private double thresholdHigh;

    @Value("${app.similarity.threshold.medium:70.0}")
    private double thresholdMedium;

    @Value("${app.similarity.max-recommendations:5}")
    private int maxRecommendations;

    public TextSimilarityService(SimilarityEngine similarityEngine,
                                 TextPreprocessor preprocessor,
                                 QuestionRepository questionRepository) {
        this.similarityEngine = similarityEngine;
        this.preprocessor = preprocessor;
        this.questionRepository = questionRepository;
    }

    public SimilarityResultDto analyzeQuestion(String newQuestionText, String subject, Long excludeQuestionId) {
        SimilarityResultDto result = new SimilarityResultDto();
        result.setQueryQuestionText(newQuestionText);
        result.setEngineName(similarityEngine.getEngineName());

        if (newQuestionText == null || newQuestionText.trim().isEmpty()) {
            result.setHighestSimilarity(0.0);
            result.setStatus("UNIQUE");
            result.setStatusDisplay("Please enter a question to analyze.");
            result.setStatusColor("secondary");
            result.setExplanation("Empty question text provided.");
            return result;
        }

        // Extract token representation for pipeline visualization
        List<String> tokens = preprocessor.tokenize(newQuestionText);
        result.setExtractedTokens(tokens);

        // Fetch candidate questions from database
        List<Question> candidates;
        if (subject != null && !subject.trim().isEmpty()) {
            candidates = questionRepository.findBySubject(subject);
            if (candidates.isEmpty()) {
                candidates = questionRepository.findAll();
            }
        } else {
            candidates = questionRepository.findAll();
        }

        List<MatchedQuestionDto> scoredMatches = new ArrayList<>();

        for (Question q : candidates) {
            if (excludeQuestionId != null && q.getId().equals(excludeQuestionId)) {
                continue;
            }

            double score = similarityEngine.calculateSimilarity(newQuestionText, q.getQuestionText());

            MatchedQuestionDto match = new MatchedQuestionDto();
            match.setId(q.getId());
            match.setQuestionText(q.getQuestionText());
            match.setSubject(q.getSubject());
            match.setUnit(q.getUnit());
            match.setCourseOutcome(q.getCourseOutcome());
            match.setBloomLevel(q.getBloomLevel());
            match.setMarks(q.getMarks());
            match.setDifficulty(q.getDifficulty());
            match.setQuestionType(q.getQuestionType());
            match.setSimilarityScore(score);

            if (score >= thresholdHigh) {
                match.setMatchClassification("HIGHLY SIMILAR");
            } else if (score >= thresholdMedium) {
                match.setMatchClassification("SIMILAR");
            } else {
                match.setMatchClassification("UNIQUE");
            }

            scoredMatches.add(match);
        }

        // Sort descending by similarity score
        scoredMatches.sort((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()));

        // Keep top N recommendations
        List<MatchedQuestionDto> topMatches = scoredMatches.stream()
                .limit(maxRecommendations)
                .collect(Collectors.toList());

        result.setMatches(topMatches);

        if (!topMatches.isEmpty()) {
            double highest = topMatches.get(0).getSimilarityScore();
            result.setHighestSimilarity(highest);

            if (highest >= thresholdHigh) {
                result.setStatus("HIGHLY_SIMILAR");
                result.setStatusDisplay("Similar Question Detected (High Similarity)");
                result.setStatusColor("danger");
                result.setDuplicate(true);
                result.setExplanation(String.format(
                        "High semantic similarity (%.1f%%) detected with existing question #%d. Both questions discuss equivalent core concepts.",
                        highest, topMatches.get(0).getId()
                ));
            } else if (highest >= thresholdMedium) {
                result.setStatus("SIMILAR");
                result.setStatusDisplay("Similar Question Detected (Review Required)");
                result.setStatusColor("warning");
                result.setDuplicate(false);
                result.setExplanation(String.format(
                        "Moderate semantic similarity (%.1f%%) detected with existing question #%d. Review recommended before saving.",
                        highest, topMatches.get(0).getId()
                ));
            } else {
                result.setStatus("UNIQUE");
                result.setStatusDisplay("The question appears to be unique.");
                result.setStatusColor("success");
                result.setDuplicate(false);
                result.setExplanation("Semantic similarity score is below the duplicate threshold. No matching concept found.");
            }
        } else {
            result.setHighestSimilarity(0.0);
            result.setStatus("UNIQUE");
            result.setStatusDisplay("The question appears to be unique.");
            result.setStatusColor("success");
            result.setDuplicate(false);
            result.setExplanation("Question bank has no existing questions for comparison.");
        }

        return result;
    }

    public double getThresholdHigh() {
        return thresholdHigh;
    }

    public double getThresholdMedium() {
        return thresholdMedium;
    }
}
