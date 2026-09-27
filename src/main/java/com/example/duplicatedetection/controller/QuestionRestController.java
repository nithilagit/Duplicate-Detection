package com.example.duplicatedetection.controller;

import com.example.duplicatedetection.dto.ApiResponse;
import com.example.duplicatedetection.dto.QuestionDto;
import com.example.duplicatedetection.dto.SimilarityCheckRequest;
import com.example.duplicatedetection.dto.SimilarityResultDto;
import com.example.duplicatedetection.entity.Faculty;
import com.example.duplicatedetection.security.SecurityUtils;
import com.example.duplicatedetection.service.DuplicateDetectionService;
import com.example.duplicatedetection.service.FacultyService;
import com.example.duplicatedetection.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionRestController {

    private final QuestionService questionService;
    private final DuplicateDetectionService duplicateDetectionService;
    private final FacultyService facultyService;

    public QuestionRestController(QuestionService questionService,
                                  DuplicateDetectionService duplicateDetectionService,
                                  FacultyService facultyService) {
        this.questionService = questionService;
        this.duplicateDetectionService = duplicateDetectionService;
        this.facultyService = facultyService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuestionDto>>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String co,
            @RequestParam(required = false) String bloom,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer marks,
            @RequestParam(required = false) String questionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Page<QuestionDto> questions = questionService.getQuestions(
                search, subject, unit, co, bloom, difficulty, marks, questionType, page, size, sortBy, direction
        );
        return ResponseEntity.ok(ApiResponse.ok(questions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionDto>> getById(@PathVariable Long id) {
        QuestionDto dto = questionService.getQuestionById(id);
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionDto>> create(@Valid @RequestBody QuestionDto dto) {
        String email = SecurityUtils.getCurrentUserEmail();
        Faculty faculty = email != null ? facultyService.findByEmail(email) : null;
        QuestionDto created = questionService.createQuestion(dto, faculty);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created, "Question created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionDto>> update(@PathVariable Long id, @Valid @RequestBody QuestionDto dto) {
        String email = SecurityUtils.getCurrentUserEmail();
        Faculty faculty = email != null ? facultyService.findByEmail(email) : null;
        QuestionDto updated = questionService.updateQuestion(id, dto, faculty);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Question updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        String email = SecurityUtils.getCurrentUserEmail();
        Faculty faculty = email != null ? facultyService.findByEmail(email) : null;
        questionService.deleteQuestion(id, faculty);
        return ResponseEntity.ok(ApiResponse.ok(null, "Question deleted successfully"));
    }

    @PostMapping("/check-similarity")
    public ResponseEntity<ApiResponse<SimilarityResultDto>> checkSimilarity(
            @Valid @RequestBody SimilarityCheckRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        Faculty faculty = email != null ? facultyService.findByEmail(email) : null;
        SimilarityResultDto result = duplicateDetectionService.checkDuplicate(request, faculty);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<ApiResponse<SimilarityResultDto>> getSimilarForExisting(@PathVariable Long id) {
        QuestionDto question = questionService.getQuestionById(id);
        SimilarityCheckRequest req = new SimilarityCheckRequest(question.getQuestionText(), question.getSubject());
        req.setExcludeQuestionId(id);

        String email = SecurityUtils.getCurrentUserEmail();
        Faculty faculty = email != null ? facultyService.findByEmail(email) : null;

        SimilarityResultDto result = duplicateDetectionService.checkDuplicate(req, faculty);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
