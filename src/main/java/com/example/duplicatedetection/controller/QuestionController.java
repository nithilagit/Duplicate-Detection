package com.example.duplicatedetection.controller;

import com.example.duplicatedetection.dto.QuestionDto;
import com.example.duplicatedetection.entity.Faculty;
import com.example.duplicatedetection.security.SecurityUtils;
import com.example.duplicatedetection.service.FacultyService;
import com.example.duplicatedetection.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final FacultyService facultyService;

    public QuestionController(QuestionService questionService, FacultyService facultyService) {
        this.questionService = questionService;
        this.facultyService = facultyService;
    }

    @GetMapping
    public String listQuestions(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String co,
            @RequestParam(required = false) String bloom,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer marks,
            @RequestParam(required = false) String questionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Model model) {

        Page<QuestionDto> questionsPage = questionService.getQuestions(
                search, subject, unit, co, bloom, difficulty, marks, questionType, page, size, sortBy, direction
        );

        model.addAttribute("questionsPage", questionsPage);
        model.addAttribute("search", search);
        model.addAttribute("selectedSubject", subject);
        model.addAttribute("selectedUnit", unit);
        model.addAttribute("selectedCo", co);
        model.addAttribute("selectedBloom", bloom);
        model.addAttribute("selectedDifficulty", difficulty);
        model.addAttribute("selectedMarks", marks);
        model.addAttribute("selectedQuestionType", questionType);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);

        // Filter dropdown options
        model.addAttribute("subjects", questionService.getSubjects());
        model.addAttribute("units", questionService.getUnits());
        model.addAttribute("courseOutcomes", questionService.getCourseOutcomes());
        model.addAttribute("bloomLevels", questionService.getBloomLevels());
        model.addAttribute("activePage", "question-bank");

        return "questions/list";
    }

    @GetMapping("/add")
    public String addQuestionForm(Model model) {
        QuestionDto dto = new QuestionDto();
        dto.setMarks(10);
        dto.setUnit("Unit I");
        dto.setCourseOutcome("CO1");
        dto.setBloomLevel("Understand");
        dto.setDifficulty("Medium");
        dto.setQuestionType("Descriptive");

        model.addAttribute("questionDto", dto);
        model.addAttribute("subjects", questionService.getSubjects());
        model.addAttribute("units", questionService.getUnits());
        model.addAttribute("courseOutcomes", questionService.getCourseOutcomes());
        model.addAttribute("bloomLevels", questionService.getBloomLevels());
        model.addAttribute("activePage", "add-question");

        return "questions/add";
    }

    @PostMapping("/add")
    public String saveQuestion(@Valid @ModelAttribute("questionDto") QuestionDto questionDto,
                               BindingResult bindingResult,
                               @RequestParam(value = "forceSave", defaultValue = "false") boolean forceSave,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("subjects", questionService.getSubjects());
            model.addAttribute("units", questionService.getUnits());
            model.addAttribute("courseOutcomes", questionService.getCourseOutcomes());
            model.addAttribute("bloomLevels", questionService.getBloomLevels());
            model.addAttribute("activePage", "add-question");
            return "questions/add";
        }

        String email = SecurityUtils.getCurrentUserEmail();
        Faculty faculty = email != null ? facultyService.findByEmail(email) : null;

        QuestionDto created = questionService.createQuestion(questionDto, faculty);
        redirectAttributes.addFlashAttribute("successMessage", "Question #" + created.getId() + " added successfully to Question Bank!");

        return "redirect:/questions";
    }

    @GetMapping("/edit/{id}")
    public String editQuestionForm(@PathVariable Long id, Model model) {
        QuestionDto dto = questionService.getQuestionById(id);
        model.addAttribute("questionDto", dto);
        model.addAttribute("subjects", questionService.getSubjects());
        model.addAttribute("units", questionService.getUnits());
        model.addAttribute("courseOutcomes", questionService.getCourseOutcomes());
        model.addAttribute("bloomLevels", questionService.getBloomLevels());
        model.addAttribute("activePage", "question-bank");

        return "questions/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateQuestion(@PathVariable Long id,
                                 @Valid @ModelAttribute("questionDto") QuestionDto questionDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("subjects", questionService.getSubjects());
            model.addAttribute("units", questionService.getUnits());
            model.addAttribute("courseOutcomes", questionService.getCourseOutcomes());
            model.addAttribute("bloomLevels", questionService.getBloomLevels());
            model.addAttribute("activePage", "question-bank");
            return "questions/edit";
        }

        String email = SecurityUtils.getCurrentUserEmail();
        Faculty faculty = email != null ? facultyService.findByEmail(email) : null;

        questionService.updateQuestion(id, questionDto, faculty);
        redirectAttributes.addFlashAttribute("successMessage", "Question #" + id + " updated successfully!");

        return "redirect:/questions";
    }

    @GetMapping("/view/{id}")
    public String viewQuestion(@PathVariable Long id, Model model) {
        QuestionDto dto = questionService.getQuestionById(id);
        model.addAttribute("question", dto);
        model.addAttribute("activePage", "question-bank");
        return "questions/view";
    }

    @PostMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String email = SecurityUtils.getCurrentUserEmail();
        Faculty faculty = email != null ? facultyService.findByEmail(email) : null;

        questionService.deleteQuestion(id, faculty);
        redirectAttributes.addFlashAttribute("successMessage", "Question #" + id + " deleted successfully from Question Bank.");

        return "redirect:/questions";
    }
}
