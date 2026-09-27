package com.example.duplicatedetection.ai;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Text preprocessing component for Question similarity analysis.
 * Normalizes text, distinguishes instructional question framing from core domain terms,
 * and extracts semantic n-grams.
 */
@Component
public class TextPreprocessor {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9\\s]");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

    // Standard grammatical stopwords
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but",
            "by", "could", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from",
            "further", "had", "has", "have", "having", "he", "her", "here", "hers", "herself", "him",
            "himself", "his", "how", "i", "if", "in", "into", "is", "it", "its", "itself", "just", "me",
            "more", "most", "my", "myself", "no", "nor", "not", "now", "of", "off", "on", "once", "only",
            "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "she",
            "should", "so", "some", "such", "than", "that", "the", "their", "theirs", "them", "themselves",
            "then", "there", "these", "they", "this", "those", "through", "to", "too", "under", "until",
            "up", "very", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom",
            "why", "with", "would", "you", "your", "yours", "yourself", "yourselves"
    ));

    // Academic instructional framing terms (question lead-ins that should carry lower semantic weight than domain entities)
    private static final Set<String> INSTRUCTIONAL_WORDS = new HashSet<>(Arrays.asList(
            "explain", "describe", "discuss", "elaborate", "illustrate", "outline", "detail", "write",
            "briefly", "short", "note", "working", "principle", "handled", "handling", "mechanism",
            "concept", "role", "importance", "various", "different", "state", "define", "give", "list",
            "mention", "provide", "diagram", "neat", "sketch", "suitable", "example", "examples", "process"
    ));

    // Synonym cluster mappings for academic terminology
    private static final Map<String, String> TERM_SYNONYMS = new HashMap<>();

    static {
        TERM_SYNONYMS.put("principle", "concept");
        TERM_SYNONYMS.put("principles", "concept");
        TERM_SYNONYMS.put("mechanism", "mechanism");
        TERM_SYNONYMS.put("mechanisms", "mechanism");
        TERM_SYNONYMS.put("algorithm", "algorithm");
        TERM_SYNONYMS.put("algorithms", "algorithm");
        TERM_SYNONYMS.put("techniques", "technique");
        TERM_SYNONYMS.put("methods", "method");
        TERM_SYNONYMS.put("protocols", "protocol");
        TERM_SYNONYMS.put("conditions", "condition");
        TERM_SYNONYMS.put("requirements", "requirement");
        TERM_SYNONYMS.put("deadlocks", "deadlock");
        TERM_SYNONYMS.put("databases", "database");
        TERM_SYNONYMS.put("transactions", "transaction");
        TERM_SYNONYMS.put("processes", "process");
        TERM_SYNONYMS.put("threads", "thread");
        TERM_SYNONYMS.put("networks", "network");
        TERM_SYNONYMS.put("occurs", "occur");
        TERM_SYNONYMS.put("occurrence", "occur");
        TERM_SYNONYMS.put("happens", "occur");
        TERM_SYNONYMS.put("happening", "occur");
        TERM_SYNONYMS.put("necessary", "required");
        TERM_SYNONYMS.put("essential", "required");
    }

    public String normalize(String text) {
        if (text == null) {
            return "";
        }
        String cleaned = NON_ALPHANUMERIC.matcher(text.toLowerCase()).replaceAll(" ");
        return MULTIPLE_SPACES.matcher(cleaned).replaceAll(" ").trim();
    }

    public List<String> tokenize(String text) {
        String normalized = normalize(text);
        if (normalized.isEmpty()) {
            return Collections.emptyList();
        }

        String[] rawTokens = normalized.split("\\s+");
        List<String> tokens = new ArrayList<>();

        for (String raw : rawTokens) {
            if (raw.length() <= 1 && !Character.isDigit(raw.charAt(0))) {
                continue;
            }
            if (!STOP_WORDS.contains(raw)) {
                String canonical = TERM_SYNONYMS.getOrDefault(raw, raw);
                tokens.add(canonical);
            }
        }
        return tokens;
    }

    /**
     * Extracts only core domain technical concepts (excluding instructional prompt framing).
     */
    public List<String> extractCoreDomainTokens(String text) {
        List<String> tokens = tokenize(text);
        List<String> core = new ArrayList<>();
        for (String t : tokens) {
            if (!INSTRUCTIONAL_WORDS.contains(t)) {
                core.add(t);
            }
        }
        return core.isEmpty() ? tokens : core;
    }

    public boolean isInstructional(String term) {
        return INSTRUCTIONAL_WORDS.contains(term);
    }
}
