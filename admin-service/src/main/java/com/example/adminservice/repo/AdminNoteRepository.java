package com.example.adminservice.repo;

import com.example.adminservice.model.AdminNote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdminNoteRepository extends MongoRepository<AdminNote, String> {
    List<AdminNote> findByTargetTypeAndTargetId(String targetType, String targetId);
}