package com.example.duplicatedetection.repository;

import com.example.duplicatedetection.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByEmail(String email);
    Optional<Faculty> findByFacultyId(String facultyId);
    boolean existsByEmail(String email);
    boolean existsByFacultyId(String facultyId);
}
