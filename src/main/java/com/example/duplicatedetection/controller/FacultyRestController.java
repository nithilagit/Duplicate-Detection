package com.example.duplicatedetection.controller;

import com.example.duplicatedetection.dto.ApiResponse;
import com.example.duplicatedetection.dto.ProfileDto;
import com.example.duplicatedetection.security.SecurityUtils;
import com.example.duplicatedetection.service.FacultyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/faculty")
public class FacultyRestController {

    private final FacultyService facultyService;

    public FacultyRestController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileDto>> getProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        ProfileDto profile = facultyService.getProfile(email);
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileDto>> updateProfile(@Valid @RequestBody ProfileDto dto) {
        String email = SecurityUtils.getCurrentUserEmail();
        ProfileDto updated = facultyService.updateProfile(email, dto);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Profile updated successfully"));
    }
}
