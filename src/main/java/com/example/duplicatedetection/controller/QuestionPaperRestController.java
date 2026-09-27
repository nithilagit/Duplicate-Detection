package com.example.duplicatedetection.controller;

import com.example.duplicatedetection.dto.ApiResponse;
import com.example.duplicatedetection.dto.PaperDistributionDto;
import com.example.duplicatedetection.dto.QuestionPaperDto;
import com.example.duplicatedetection.entity.Faculty;
import com.example.duplicatedetection.security.SecurityUtils;
import com.example.duplicatedetection.service.FacultyService;
import com.example.duplicatedetection.service.QuestionPaperService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question-papers")
public class QuestionPaperRestController {

    private final QuestionPaperService paperService;
    private final FacultyService facultyService;

    public QuestionPaperRestController(QuestionPaperService paperService, FacultyService facultyService) {
        this.paperService = paperService;
        this.facultyService = facultyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuestionPaperDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(paperService.getAllPapers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionPaperDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(paperService.getPaperById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionPaperDto>> create(@Valid @RequestBody QuestionPaperDto dto) {
        String email = SecurityUtils.getCurrentUserEmail();
        Faculty faculty = email != null ? facultyService.findByEmail(email) : null;
        QuestionPaperDto created = paperService.createPaper(dto, faculty);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Question Paper generated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        String email = SecurityUtils.getCurrentUserEmail();
        Faculty faculty = email != null ? facultyService.findByEmail(email) : null;
        paperService.deletePaper(id, faculty);
        return ResponseEntity.ok(ApiResponse.ok(null, "Question Paper deleted"));
    }

    @PostMapping("/compute-distribution")
    public ResponseEntity<ApiResponse<PaperDistributionDto>> computeDistribution(@RequestBody List<Long> questionIds) {
        PaperDistributionDto distribution = paperService.computeDistribution(questionIds);
        return ResponseEntity.ok(ApiResponse.ok(distribution));
    }
}
