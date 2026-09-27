package com.example.duplicatedetection.config;

import com.example.duplicatedetection.entity.Faculty;
import com.example.duplicatedetection.security.SecurityUtils;
import com.example.duplicatedetection.service.FacultyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller advice providing dynamic global model attributes across all Thymeleaf views.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    private final FacultyService facultyService;

    @Value("${app.demo.faculty.department:Artificial Intelligence and Data Science}")
    private String defaultDepartment;

    @Value("${app.demo.faculty.email:faculty@eec.srmrmp.edu.in}")
    private String defaultEmail;

    @Value("${app.curriculum.subjects:Discrete Mathematics,Computer Networks,Advanced Data Structures and Algorithms,Embedded System Design,Machine Learning Techniques,Object Oriented Programming using Java}")
    private String configuredSubjects;

    public GlobalModelAttributes(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @ModelAttribute("currentFaculty")
    public Faculty populateCurrentFaculty() {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email != null) {
            return facultyService.findByEmail(email);
        }
        return null;
    }

    @ModelAttribute("defaultDepartment")
    public String populateDefaultDepartment() {
        return defaultDepartment;
    }

    @ModelAttribute("defaultEmail")
    public String populateDefaultEmail() {
        return defaultEmail;
    }

    @ModelAttribute("subjects")
    public List<String> populateSubjects() {
        if (configuredSubjects == null || configuredSubjects.trim().isEmpty()) {
            return List.of();
        }
        return Arrays.stream(configuredSubjects.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @ModelAttribute("availableSubjects")
    public List<String> populateAvailableSubjects() {
        return populateSubjects();
    }
}

