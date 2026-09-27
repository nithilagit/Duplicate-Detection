package com.example.duplicatedetection.ai;

import java.util.Map;

/**
 * Pluggable AI / NLP Similarity Engine Interface.
 * Allows switching between TF-IDF Cosine, ONNX Sentence Transformers,
 * and high-dimensional semantic embeddings.
 */
public interface SimilarityEngine {

    /**
     * Compute semantic similarity between two question texts as a percentage [0.0 - 100.0].
     */
    double calculateSimilarity(String text1, String text2);

    /**
     * Generate embedding vector representation for a question text.
     */
    Map<String, Double> generateEmbeddingVector(String text);

    /**
     * Compute cosine similarity between two embedding vectors [0.0 - 1.0].
     */
    double calculateCosineSimilarity(Map<String, Double> vector1, Map<String, Double> vector2);

    /**
     * Name of the active similarity algorithm/model.
     */
    String getEngineName();
}
