package com.example.adminservice.repo;

import com.example.adminservice.model.AdminActionLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdminActionLogRepository extends MongoRepository<AdminActionLog, String> {
    List<AdminActionLog> findByAdminUserId(String adminUserId);
    List<AdminActionLog> findByTargetTypeAndTargetId(String targetType, String targetId);
}