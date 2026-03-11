package com.example.adminservice.service;

import com.example.adminservice.dtos.AdminActionLogResponse;
import com.example.adminservice.dtos.AdminNoteResponse;
import com.example.adminservice.dtos.AuditLogResponse;
import com.example.adminservice.dtos.CreateAdminNoteRequest;
import com.example.adminservice.dtos.CreateModerationActionRequest;
import com.example.adminservice.dtos.ModerationActionResponse;
import com.example.adminservice.model.AdminActionLog;
import com.example.adminservice.model.AdminNote;
import com.example.adminservice.model.AuditLog;
import com.example.adminservice.model.ModerationAction;
import com.example.adminservice.repo.AdminActionLogRepository;
import com.example.adminservice.repo.AdminNoteRepository;
import com.example.adminservice.repo.AuditLogRepository;
import com.example.adminservice.repo.ModerationActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminManagementService {

    private final AdminActionLogRepository adminActionLogRepository;
    private final AdminNoteRepository adminNoteRepository;
    private final AuditLogRepository auditLogRepository;
    private final ModerationActionRepository moderationActionRepository;

    public AdminNoteResponse createNote(CreateAdminNoteRequest request) {
        AdminNote note = AdminNote.builder()
                .adminUserId("ADMIN")
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .note(request.getNote())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        AdminNote saved = adminNoteRepository.save(note);
        return toAdminNoteResponse(saved);
    }

    public List<AdminNoteResponse> getNotes(String targetType, String targetId) {
        return adminNoteRepository.findByTargetTypeAndTargetId(targetType, targetId)
                .stream()
                .map(this::toAdminNoteResponse)
                .toList();
    }

    public ModerationActionResponse createModerationAction(CreateModerationActionRequest request) {
        ModerationAction action = ModerationAction.builder()
                .adminUserId("ADMIN")
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .action(request.getAction())
                .reason(request.getReason())
                .notes(request.getNotes())
                .createdAt(Instant.now())
                .build();

        ModerationAction saved = moderationActionRepository.save(action);

        logAdminAction(
                "MODERATION_" + request.getAction(),
                request.getTargetType(),
                request.getTargetId(),
                request.getReason()
        );

        return toModerationActionResponse(saved);
    }

    public List<ModerationActionResponse> getModerationActions(String targetType, String targetId) {
        return moderationActionRepository.findByTargetTypeAndTargetId(targetType, targetId)
                .stream()
                .map(this::toModerationActionResponse)
                .toList();
    }

    public List<AdminActionLogResponse> getActionLogs(String targetType, String targetId) {
        return adminActionLogRepository.findByTargetTypeAndTargetId(targetType, targetId)
                .stream()
                .map(this::toAdminActionLogResponse)
                .toList();
    }

    public List<AuditLogResponse> getAuditLogs(String entityType, String entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId)
                .stream()
                .map(this::toAuditLogResponse)
                .toList();
    }

    public AdminActionLog logAdminAction(String actionType, String targetType, String targetId, String description) {
        AdminActionLog log = AdminActionLog.builder()
                .adminUserId("ADMIN")
                .actionType(actionType)
                .targetType(targetType)
                .targetId(targetId)
                .description(description)
                .createdAt(Instant.now())
                .build();

        return adminActionLogRepository.save(log);
    }

    public AuditLog logAudit(String eventType, String entityType, String entityId, String performedBy, String description) {
        AuditLog log = AuditLog.builder()
                .eventType(eventType)
                .entityType(entityType)
                .entityId(entityId)
                .performedBy(performedBy)
                .description(description)
                .createdAt(Instant.now())
                .build();

        return auditLogRepository.save(log);
    }

    private AdminNoteResponse toAdminNoteResponse(AdminNote note) {
        return AdminNoteResponse.builder()
                .id(note.getId())
                .adminUserId(note.getAdminUserId())
                .targetType(note.getTargetType())
                .targetId(note.getTargetId())
                .note(note.getNote())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }

    private ModerationActionResponse toModerationActionResponse(ModerationAction action) {
        return ModerationActionResponse.builder()
                .id(action.getId())
                .adminUserId(action.getAdminUserId())
                .targetType(action.getTargetType())
                .targetId(action.getTargetId())
                .action(action.getAction())
                .reason(action.getReason())
                .notes(action.getNotes())
                .createdAt(action.getCreatedAt())
                .build();
    }

    private AdminActionLogResponse toAdminActionLogResponse(AdminActionLog log) {
        return AdminActionLogResponse.builder()
                .id(log.getId())
                .adminUserId(log.getAdminUserId())
                .actionType(log.getActionType())
                .targetType(log.getTargetType())
                .targetId(log.getTargetId())
                .description(log.getDescription())
                .createdAt(log.getCreatedAt())
                .build();
    }

    private AuditLogResponse toAuditLogResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .eventType(log.getEventType())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .performedBy(log.getPerformedBy())
                .description(log.getDescription())
                .metadata(log.getMetadata())
                .createdAt(log.getCreatedAt())
                .build();
    }
}