package com.example.duplicatedetection.service;

import com.example.duplicatedetection.dto.PaperDistributionDto;
import com.example.duplicatedetection.dto.QuestionPaperDto;
import com.example.duplicatedetection.dto.QuestionPaperItemDto;
import com.example.duplicatedetection.entity.*;
import com.example.duplicatedetection.exception.ResourceNotFoundException;
import com.example.duplicatedetection.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionPaperService {

    private final QuestionPaperRepository paperRepository;
    private final QuestionPaperQuestionRepository paperQuestionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionUsageRepository usageRepository;
    private final ActivityLogService activityLogService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    public QuestionPaperService(QuestionPaperRepository paperRepository,
                                QuestionPaperQuestionRepository paperQuestionRepository,
                                QuestionRepository questionRepository,
                                QuestionUsageRepository usageRepository,
                                ActivityLogService activityLogService) {
        this.paperRepository = paperRepository;
        this.paperQuestionRepository = paperQuestionRepository;
        this.questionRepository = questionRepository;
        this.usageRepository = usageRepository;
        this.activityLogService = activityLogService;
    }

    public List<QuestionPaperDto> getAllPapers() {
        return paperRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public QuestionPaperDto getPaperById(Long id) {
        QuestionPaper paper = paperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question paper not found with ID: " + id));
        return toDto(paper);
    }

    @Transactional
    public QuestionPaperDto createPaper(QuestionPaperDto dto, Faculty faculty) {
        QuestionPaper paper = new QuestionPaper();
        paper.setTitle(dto.getTitle().trim());
        paper.setSubject(dto.getSubject());
        paper.setExamCode(dto.getExamCode());
        paper.setAcademicYear(dto.getAcademicYear());
        paper.setSemester(dto.getSemester());
        paper.setTotalMarks(dto.getTotalMarks());
        paper.setInstructions(dto.getInstructions());
        paper.setCreatedBy(faculty);

        QuestionPaper savedPaper = paperRepository.save(paper);

        int qNumber = 1;
        int calculatedMarks = 0;

        if (dto.getQuestions() != null) {
            for (QuestionPaperItemDto item : dto.getQuestions()) {
                Question question = questionRepository.findById(item.getQuestionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + item.getQuestionId()));

                int marks = item.getAllocatedMarks() != null ? item.getAllocatedMarks() : question.getMarks();
                calculatedMarks += marks;

                QuestionPaperQuestion qpq = new QuestionPaperQuestion(
                        savedPaper,
                        question,
                        item.getQuestionNumber() != null ? item.getQuestionNumber() : qNumber++,
                        item.getSectionName() != null ? item.getSectionName() : "Part A",
                        marks
                );
                paperQuestionRepository.save(qpq);

                // Increment question usage count
                int count = question.getUsageCount() != null ? question.getUsageCount() : 0;
                question.setUsageCount(count + 1);
                questionRepository.save(question);

                // Record usage history
                usageRepository.save(new QuestionUsage(question, savedPaper));
            }
        }

        // If total marks was not explicitly provided or was 0, update with calculated sum
        if (savedPaper.getTotalMarks() == null || savedPaper.getTotalMarks() == 0) {
            savedPaper.setTotalMarks(calculatedMarks);
            savedPaper = paperRepository.save(savedPaper);
        }

        activityLogService.logActivity(faculty, "CREATED_QUESTION_PAPER",
                String.format("Created question paper '%s' with %d questions",
                        savedPaper.getTitle(), dto.getQuestions() != null ? dto.getQuestions().size() : 0));

        return toDto(savedPaper);
    }

    @Transactional
    public void deletePaper(Long id, Faculty faculty) {
        QuestionPaper paper = paperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question paper not found with ID: " + id));
        paperQuestionRepository.deleteByQuestionPaperId(id);
        paperRepository.delete(paper);

        activityLogService.logActivity(faculty, "DELETED_QUESTION_PAPER",
                String.format("Deleted question paper #%d (%s)", id, paper.getTitle()));
    }

    /**
     * Compute real-time distributions and advisory warnings for questions selected in a paper.
     */
    public PaperDistributionDto computeDistribution(List<Long> questionIds) {
        PaperDistributionDto dist = new PaperDistributionDto();
        if (questionIds == null || questionIds.isEmpty()) {
            dist.setAverageDifficulty("N/A");
            dist.setBalanced(true);
            return dist;
        }

        List<Question> questions = questionRepository.findAllById(questionIds);
        dist.setTotalQuestions(questions.size());

        int totalMarks = 0;
        Map<String, Integer> unitMap = new LinkedHashMap<>();
        Map<String, Integer> coMap = new LinkedHashMap<>();
        Map<String, Integer> bloomMap = new LinkedHashMap<>();
        Map<String, Integer> diffMap = new LinkedHashMap<>();

        // Difficulty points for average score: Easy = 1, Medium = 2, Hard = 3
        double difficultyScoreSum = 0;

        for (Question q : questions) {
            totalMarks += q.getMarks();

            unitMap.put(q.getUnit(), unitMap.getOrDefault(q.getUnit(), 0) + 1);
            coMap.put(q.getCourseOutcome(), coMap.getOrDefault(q.getCourseOutcome(), 0) + 1);
            bloomMap.put(q.getBloomLevel(), bloomMap.getOrDefault(q.getBloomLevel(), 0) + 1);
            diffMap.put(q.getDifficulty(), diffMap.getOrDefault(q.getDifficulty(), 0) + 1);

            if ("Easy".equalsIgnoreCase(q.getDifficulty())) {
                difficultyScoreSum += 1;
            } else if ("Hard".equalsIgnoreCase(q.getDifficulty())) {
                difficultyScoreSum += 3;
            } else {
                difficultyScoreSum += 2;
            }
        }

        dist.setTotalMarks(totalMarks);
        dist.setUnitDistribution(unitMap);
        dist.setBloomDistribution(bloomMap);
        dist.setDifficultyDistribution(diffMap);

        // Calculate CO percentages
        Map<String, Double> coPercentages = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : coMap.entrySet()) {
            double pct = ((double) entry.getValue() / questions.size()) * 100.0;
            coPercentages.put(entry.getKey(), Math.round(pct * 10.0) / 10.0);
        }
        dist.setCoPercentages(coPercentages);

        // Average difficulty label
        double avgScore = difficultyScoreSum / questions.size();
        if (avgScore <= 1.4) {
            dist.setAverageDifficulty(String.format("Easy (%.1f / 3.0)", avgScore));
        } else if (avgScore >= 2.4) {
            dist.setAverageDifficulty(String.format("Hard (%.1f / 3.0)", avgScore));
        } else {
            dist.setAverageDifficulty(String.format("Medium (%.1f / 3.0)", avgScore));
        }

        // Advisory balance checks
        List<String> warnings = new ArrayList<>();
        if (questions.size() >= 5) {
            // Check unit coverage
            if (unitMap.size() < 3) {
                warnings.add("Limited unit coverage: Only " + unitMap.size() + " distinct unit(s) represented.");
            }

            // Check Bloom's taxonomy variety
            boolean hasHigherOrder = bloomMap.containsKey("Apply") || bloomMap.containsKey("Analyze") ||
                                     bloomMap.containsKey("Evaluate") || bloomMap.containsKey("Create");
            if (!hasHigherOrder) {
                warnings.add("Cognitive balance warning: Paper lacks higher-order thinking levels (Apply, Analyze, Evaluate, Create).");
            }

            // Check difficulty bias
            int easyCount = diffMap.getOrDefault("Easy", 0);
            int hardCount = diffMap.getOrDefault("Hard", 0);
            if (easyCount > questions.size() * 0.7) {
                warnings.add("Difficulty skew: Over 70% of questions are classified as 'Easy'.");
            } else if (hardCount > questions.size() * 0.6) {
                warnings.add("Difficulty skew: Over 60% of questions are classified as 'Hard'.");
            }
        }

        dist.setAdvisoryWarnings(warnings);
        dist.setBalanced(warnings.isEmpty());

        return dist;
    }

    public QuestionPaperDto toDto(QuestionPaper paper) {
        QuestionPaperDto dto = new QuestionPaperDto();
        dto.setId(paper.getId());
        dto.setTitle(paper.getTitle());
        dto.setSubject(paper.getSubject());
        dto.setExamCode(paper.getExamCode());
        dto.setAcademicYear(paper.getAcademicYear());
        dto.setSemester(paper.getSemester());
        dto.setTotalMarks(paper.getTotalMarks());
        dto.setInstructions(paper.getInstructions());

        if (paper.getCreatedBy() != null) {
            dto.setCreatedByName(paper.getCreatedBy().getName());
        } else {
            dto.setCreatedByName("Faculty Member");
        }

        if (paper.getCreatedAt() != null) {
            dto.setCreatedAtFormatted(paper.getCreatedAt().format(DATE_FORMATTER));
        }

        List<QuestionPaperQuestion> items = paperQuestionRepository.findByQuestionPaperIdOrderByQuestionNumberAsc(paper.getId());
        List<QuestionPaperItemDto> itemDtos = new ArrayList<>();
        List<Long> questionIds = new ArrayList<>();

        for (QuestionPaperQuestion item : items) {
            Question q = item.getQuestion();
            QuestionPaperItemDto itemDto = new QuestionPaperItemDto();
            itemDto.setQuestionId(q.getId());
            itemDto.setQuestionNumber(item.getQuestionNumber());
            itemDto.setSectionName(item.getSectionName());
            itemDto.setAllocatedMarks(item.getAllocatedMarks());
            itemDto.setQuestionText(q.getQuestionText());
            itemDto.setUnit(q.getUnit());
            itemDto.setCourseOutcome(q.getCourseOutcome());
            itemDto.setBloomLevel(q.getBloomLevel());
            itemDto.setDifficulty(q.getDifficulty());

            itemDtos.add(itemDto);
            questionIds.add(q.getId());
        }

        dto.setQuestions(itemDtos);
        dto.setDistribution(computeDistribution(questionIds));

        return dto;
    }
}
