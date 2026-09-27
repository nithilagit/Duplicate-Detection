package com.example.duplicatedetection.service;

import com.example.duplicatedetection.dto.PasswordChangeDto;
import com.example.duplicatedetection.dto.ProfileDto;
import com.example.duplicatedetection.entity.Faculty;
import com.example.duplicatedetection.exception.ResourceNotFoundException;
import com.example.duplicatedetection.repository.FacultyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    public FacultyService(FacultyRepository facultyRepository,
                          PasswordEncoder passwordEncoder,
                          ActivityLogService activityLogService) {
        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
        this.activityLogService = activityLogService;
    }

    public Faculty findByEmail(String email) {
        if (email == null || "anonymousUser".equalsIgnoreCase(email)) {
            return null;
        }
        return facultyRepository.findByEmail(email).orElse(null);
    }

    public Faculty findById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + id));
    }

    public ProfileDto getProfile(String email) {
        Faculty faculty = findByEmail(email);
        return new ProfileDto(
                faculty.getId(),
                faculty.getFacultyId(),
                faculty.getName(),
                faculty.getEmail(),
                faculty.getDepartment(),
                faculty.getRole()
        );
    }

    @Transactional
    public ProfileDto updateProfile(String email, ProfileDto dto) {
        Faculty faculty = findByEmail(email);
        faculty.setName(dto.getName());
        faculty.setDepartment(dto.getDepartment());
        facultyRepository.save(faculty);

        activityLogService.logActivity(faculty, "PROFILE_UPDATED", "Updated profile details for " + faculty.getName());

        return new ProfileDto(
                faculty.getId(),
                faculty.getFacultyId(),
                faculty.getName(),
                faculty.getEmail(),
                faculty.getDepartment(),
                faculty.getRole()
        );
    }

    @Transactional
    public void changePassword(String email, PasswordChangeDto dto) {
        Faculty faculty = findByEmail(email);

        if (!passwordEncoder.matches(dto.getCurrentPassword(), faculty.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        faculty.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        facultyRepository.save(faculty);

        activityLogService.logActivity(faculty, "PASSWORD_CHANGED", "Password successfully changed");
    }
}
