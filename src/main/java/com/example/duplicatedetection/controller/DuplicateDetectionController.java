package com.example.duplicatedetection.controller;

import com.example.duplicatedetection.ai.TextSimilarityService;
import com.example.duplicatedetection.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ai")
public class DuplicateDetectionController {

    private final QuestionService questionService;
    private final TextSimilarityService similarityService;

    public DuplicateDetectionController(QuestionService questionService, TextSimilarityService similarityService) {
        this.questionService = questionService;
        this.similarityService = similarityService;
    }

    @GetMapping("/detect")
    public String detectPage(Model model) {
        model.addAttribute("subjects", questionService.getSubjects());
        model.addAttribute("thresholdHigh", similarityService.getThresholdHigh());
        model.addAttribute("thresholdMedium", similarityService.getThresholdMedium());
        model.addAttribute("activePage", "ai-detect");
        return "ai/detect";
    }
}
