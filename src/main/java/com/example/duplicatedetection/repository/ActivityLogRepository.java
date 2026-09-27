package com.example.duplicatedetection.repository;

import com.example.duplicatedetection.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop15ByOrderByCreatedAtDesc();
    List<ActivityLog> findByFacultyIdOrderByCreatedAtDesc(Long facultyId);
}
