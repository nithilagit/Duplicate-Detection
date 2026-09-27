package com.example.duplicatedetection.service;

import com.example.duplicatedetection.ai.TextSimilarityService;
import com.example.duplicatedetection.dto.MatchedQuestionDto;
import com.example.duplicatedetection.dto.SimilarityCheckRequest;
import com.example.duplicatedetection.dto.SimilarityResultDto;
import com.example.duplicatedetection.entity.Faculty;
import com.example.duplicatedetection.entity.Question;
import com.example.duplicatedetection.entity.SimilarityCheck;
import com.example.duplicatedetection.repository.QuestionRepository;
import com.example.duplicatedetection.repository.SimilarityCheckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DuplicateDetectionService {

    private final TextSimilarityService textSimilarityService;
    private final SimilarityCheckRepository similarityCheckRepository;
    private final QuestionRepository questionRepository;
    private final ActivityLogService activityLogService;

    public DuplicateDetectionService(TextSimilarityService textSimilarityService,
                                     SimilarityCheckRepository similarityCheckRepository,
                                     QuestionRepository questionRepository,
                                     ActivityLogService activityLogService) {
        this.textSimilarityService = textSimilarityService;
        this.similarityCheckRepository = similarityCheckRepository;
        this.questionRepository = questionRepository;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public SimilarityResultDto checkDuplicate(SimilarityCheckRequest request, Faculty faculty) {
        SimilarityResultDto result = textSimilarityService.analyzeQuestion(
                request.getQuestionText(),
                request.getSubject(),
                request.getExcludeQuestionId()
        );

        // Record check in audit log
        try {
            Question matchedEntity = null;
            String matchedText = null;
            if (!result.getMatches().isEmpty()) {
                MatchedQuestionDto top = result.getMatches().get(0);
                matchedText = top.getQuestionText();
                matchedEntity = questionRepository.findById(top.getId()).orElse(null);
            }

            SimilarityCheck check = new SimilarityCheck(
                    request.getQuestionText(),
                    matchedEntity,
                    matchedText,
                    result.getHighestSimilarity(),
                    result.getStatus(),
                    result.getExplanation(),
                    faculty
            );
            similarityCheckRepository.save(check);

            if (faculty != null) {
                activityLogService.logActivity(faculty, "DUPLICATE_CHECK",
                        String.format("Checked question similarity. Score: %.1f%% (%s)",
                                result.getHighestSimilarity(), result.getStatus()));
            }
        } catch (Exception e) {
            // Audit persistence error should not break the user's similarity check
        }

        return result;
    }

    public List<SimilarityCheck> getRecentChecks() {
        return similarityCheckRepository.findTop10ByOrderByCheckedAtDesc();
    }
}
