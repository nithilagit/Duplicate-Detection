package com.example.duplicatedetection.controller;

import com.example.duplicatedetection.dto.QuestionPaperDto;
import com.example.duplicatedetection.entity.Faculty;
import com.example.duplicatedetection.security.SecurityUtils;
import com.example.duplicatedetection.service.FacultyService;
import com.example.duplicatedetection.service.QuestionPaperService;
import com.example.duplicatedetection.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/papers")
public class QuestionPaperController {

    private final QuestionPaperService paperService;
    private final QuestionService questionService;
    private final FacultyService facultyService;

    public QuestionPaperController(QuestionPaperService paperService,
                                   QuestionService questionService,
                                   FacultyService facultyService) {
        this.paperService = paperService;
        this.questionService = questionService;
        this.facultyService = facultyService;
    }

    @GetMapping
    public String listPapers(Model model) {
        model.addAttribute("papers", paperService.getAllPapers());
        model.addAttribute("activePage", "paper-builder");
        return "papers/list";
    }

    @GetMapping("/builder")
    public String paperBuilder(Model model) {
        model.addAttribute("subjects", questionService.getSubjects());
        model.addAttribute("units", questionService.getUnits());
        model.addAttribute("activePage", "paper-builder");
        return "papers/builder";
    }

    @GetMapping("/view/{id}")
    public String viewPaper(@PathVariable Long id, Model model) {
        QuestionPaperDto paper = paperService.getPaperById(id);
        model.addAttribute("paper", paper);
        model.addAttribute("activePage", "paper-builder");
        return "papers/view";
    }

    @GetMapping("/print/{id}")
    public String printPaper(@PathVariable Long id, Model model) {
        QuestionPaperDto paper = paperService.getPaperById(id);
        model.addAttribute("paper", paper);
        return "papers/print";
    }

    @PostMapping("/delete/{id}")
    public String deletePaper(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String email = SecurityUtils.getCurrentUserEmail();
        Faculty faculty = email != null ? facultyService.findByEmail(email) : null;

        paperService.deletePaper(id, faculty);
        redirectAttributes.addFlashAttribute("successMessage", "Question paper #" + id + " deleted successfully.");

        return "redirect:/papers";
    }
}
