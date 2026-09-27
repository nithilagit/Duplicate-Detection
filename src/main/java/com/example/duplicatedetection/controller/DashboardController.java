package com.example.duplicatedetection.controller;

import com.example.duplicatedetection.dto.DashboardStatsDto;
import com.example.duplicatedetection.entity.Faculty;
import com.example.duplicatedetection.security.SecurityUtils;
import com.example.duplicatedetection.service.AnalyticsService;
import com.example.duplicatedetection.service.FacultyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final AnalyticsService analyticsService;
    private final FacultyService facultyService;

    public DashboardController(AnalyticsService analyticsService, FacultyService facultyService) {
        this.analyticsService = analyticsService;
        this.facultyService = facultyService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String email = SecurityUtils.getCurrentUserEmail();
        Faculty currentFaculty = email != null ? facultyService.findByEmail(email) : null;
        DashboardStatsDto stats = analyticsService.getDashboardStats();

        model.addAttribute("faculty", currentFaculty);
        model.addAttribute("stats", stats);
        model.addAttribute("activePage", "dashboard");

        return "dashboard/index";
    }
}
