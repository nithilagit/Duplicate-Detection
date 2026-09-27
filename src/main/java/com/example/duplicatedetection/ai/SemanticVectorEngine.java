package com.example.duplicatedetection.ai;

import org.springframework.stereotype.Component;

import java.util.*;

/**
 * High-performance semantic vector engine based on domain-weighted n-gram embeddings,
 * Cosine Similarity, and semantic core overlap.
 */
@Component
public class SemanticVectorEngine implements SimilarityEngine {

    private final TextPreprocessor preprocessor;

    public SemanticVectorEngine(TextPreprocessor preprocessor) {
        this.preprocessor = preprocessor;
    }

    @Override
    public String getEngineName() {
        return "Domain-Weighted N-Gram Cosine Embedding Engine (v1.2)";
    }

    @Override
    public Map<String, Double> generateEmbeddingVector(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> tokens = preprocessor.tokenize(text);
        if (tokens.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Double> rawWeights = new HashMap<>();

        // 1. Unigram weights
        for (String term : tokens) {
            boolean instructional = preprocessor.isInstructional(term);
            double baseWeight = instructional ? 0.4 : 3.0; // Core technical domain terms receive high priority
            rawWeights.put(term, rawWeights.getOrDefault(term, 0.0) + baseWeight);
        }

        // 2. Bigram weights for technical phrases (e.g. congestion_control, deadlock_conditions)
        for (int i = 0; i < tokens.size() - 1; i++) {
            String t1 = tokens.get(i);
            String t2 = tokens.get(i + 1);
            boolean bothDomain = !preprocessor.isInstructional(t1) && !preprocessor.isInstructional(t2);
            double bigramWeight = bothDomain ? 4.5 : 1.0;
            String bigram = t1 + "_" + t2;
            rawWeights.put(bigram, rawWeights.getOrDefault(bigram, 0.0) + bigramWeight);
        }

        // Normalize to L2 unit vector
        double sumSquares = 0.0;
        for (double w : rawWeights.values()) {
            sumSquares += w * w;
        }

        double norm = Math.sqrt(sumSquares);
        if (norm == 0.0) {
            return Collections.emptyMap();
        }

        Map<String, Double> normalizedVector = new HashMap<>();
        for (Map.Entry<String, Double> entry : rawWeights.entrySet()) {
            normalizedVector.put(entry.getKey(), entry.getValue() / norm);
        }

        return normalizedVector;
    }

    @Override
    public double calculateCosineSimilarity(Map<String, Double> vector1, Map<String, Double> vector2) {
        if (vector1 == null || vector2 == null || vector1.isEmpty() || vector2.isEmpty()) {
            return 0.0;
        }

        Map<String, Double> small = vector1.size() <= vector2.size() ? vector1 : vector2;
        Map<String, Double> large = vector1.size() <= vector2.size() ? vector2 : vector1;

        double dotProduct = 0.0;
        for (Map.Entry<String, Double> entry : small.entrySet()) {
            Double valLarge = large.get(entry.getKey());
            if (valLarge != null) {
                dotProduct += entry.getValue() * valLarge;
            }
        }

        return Math.max(0.0, Math.min(1.0, dotProduct));
    }

    @Override
    public double calculateSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0;
        }

        String norm1 = preprocessor.normalize(text1);
        String norm2 = preprocessor.normalize(text2);

        if (norm1.isEmpty() || norm2.isEmpty()) {
            return 0.0;
        }

        if (norm1.equalsIgnoreCase(norm2)) {
            return 100.0;
        }

        // Vector Cosine Similarity
        Map<String, Double> vec1 = generateEmbeddingVector(text1);
        Map<String, Double> vec2 = generateEmbeddingVector(text2);
        double cosine = calculateCosineSimilarity(vec1, vec2);

        // Core Domain Entity Overlap (measures question subject alignment)
        List<String> core1 = preprocessor.extractCoreDomainTokens(text1);
        List<String> core2 = preprocessor.extractCoreDomainTokens(text2);

        Set<String> set1 = new HashSet<>(core1);
        Set<String> set2 = new HashSet<>(core2);

        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        double overlapCoefficient = 0.0;
        int minSize = Math.min(set1.size(), set2.size());
        if (minSize > 0) {
            overlapCoefficient = ((double) intersection.size()) / minSize;
        }

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        double jaccard = union.isEmpty() ? 0.0 : ((double) intersection.size()) / union.size();

        // Harmonic combination: 45% Cosine + 35% Core Overlap + 20% Jaccard
        double hybrid = (0.45 * cosine) + (0.35 * overlapCoefficient) + (0.20 * jaccard);

        double percentage = hybrid * 100.0;
        return Math.round(percentage * 10.0) / 10.0;
    }
}
