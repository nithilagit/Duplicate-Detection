package com.example.duplicatedetection.controller;

import com.example.duplicatedetection.dto.PasswordChangeDto;
import com.example.duplicatedetection.dto.ProfileDto;
import com.example.duplicatedetection.security.SecurityUtils;
import com.example.duplicatedetection.service.FacultyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final FacultyService facultyService;

    public ProfileController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public String viewProfile(Model model) {
        String email = SecurityUtils.getCurrentUserEmail();
        ProfileDto profile = facultyService.getProfile(email);

        model.addAttribute("profile", profile);
        model.addAttribute("passwordChangeDto", new PasswordChangeDto());
        model.addAttribute("activePage", "profile");
        return "profile/view";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profile") ProfileDto profileDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        String email = SecurityUtils.getCurrentUserEmail();

        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordChangeDto", new PasswordChangeDto());
            model.addAttribute("activePage", "profile");
            return "profile/view";
        }

        try {
            facultyService.updateProfile(email, profileDto);
            redirectAttributes.addFlashAttribute("profileSuccess", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("profileError", e.getMessage());
        }

        return "redirect:/profile";
    }

    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute("passwordChangeDto") PasswordChangeDto passwordDto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        String email = SecurityUtils.getCurrentUserEmail();

        if (bindingResult.hasErrors()) {
            model.addAttribute("profile", facultyService.getProfile(email));
            model.addAttribute("activePage", "profile");
            return "profile/view";
        }

        try {
            facultyService.changePassword(email, passwordDto);
            redirectAttributes.addFlashAttribute("passwordSuccess", "Password changed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("passwordError", e.getMessage());
        }

        return "redirect:/profile";
    }
}
