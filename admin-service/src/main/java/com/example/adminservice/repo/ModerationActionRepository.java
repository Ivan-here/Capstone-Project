package com.example.adminservice.repo;

import com.example.adminservice.model.ModerationAction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ModerationActionRepository extends MongoRepository<ModerationAction, String> {
    List<ModerationAction> findByTargetTypeAndTargetId(String targetType, String targetId);
    List<ModerationAction> findByAdminUserId(String adminUserId);
}