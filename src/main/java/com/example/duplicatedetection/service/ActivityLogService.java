package com.example.duplicatedetection.service;

import com.example.duplicatedetection.entity.ActivityLog;
import com.example.duplicatedetection.entity.Faculty;
import com.example.duplicatedetection.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Transactional
    public void logActivity(Faculty faculty, String action, String details) {
        ActivityLog log = new ActivityLog(faculty, action, details, "127.0.0.1");
        activityLogRepository.save(log);
    }

    public List<ActivityLog> getRecentActivities() {
        return activityLogRepository.findTop15ByOrderByCreatedAtDesc();
    }
}
