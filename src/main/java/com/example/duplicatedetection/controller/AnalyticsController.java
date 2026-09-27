package com.example.duplicatedetection.controller;

import com.example.duplicatedetection.dto.DuplicateReportDto;
import com.example.duplicatedetection.service.AnalyticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reports")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public String viewReports(Model model) {
        DuplicateReportDto report = analyticsService.getDuplicateReports();
        model.addAttribute("report", report);
        model.addAttribute("activePage", "reports");
        return "reports/analytics";
    }
}
