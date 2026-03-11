package com.example.adminservice.controller;

import com.example.adminservice.dtos.AdminActionLogResponse;
import com.example.adminservice.dtos.AdminNoteResponse;
import com.example.adminservice.dtos.AuditLogResponse;
import com.example.adminservice.dtos.CreateAdminNoteRequest;
import com.example.adminservice.dtos.CreateModerationActionRequest;
import com.example.adminservice.dtos.ModerationActionResponse;
import com.example.adminservice.service.AdminManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/management")
@RequiredArgsConstructor
public class AdminManagementController {

    private final AdminManagementService adminManagementService;

    @PostMapping("/notes")
    public AdminNoteResponse createNote(@RequestBody CreateAdminNoteRequest request) {
        return adminManagementService.createNote(request);
    }

    @GetMapping("/notes/{targetType}/{targetId}")
    public List<AdminNoteResponse> getNotes(
            @PathVariable String targetType,
            @PathVariable String targetId
    ) {
        return adminManagementService.getNotes(targetType, targetId);
    }

    @PostMapping("/moderation-actions")
    public ModerationActionResponse createModerationAction(
            @RequestBody CreateModerationActionRequest request
    ) {
        return adminManagementService.createModerationAction(request);
    }

    @GetMapping("/moderation-actions/{targetType}/{targetId}")
    public List<ModerationActionResponse> getModerationActions(
            @PathVariable String targetType,
            @PathVariable String targetId
    ) {
        return adminManagementService.getModerationActions(targetType, targetId);
    }

    @GetMapping("/action-logs/{targetType}/{targetId}")
    public List<AdminActionLogResponse> getActionLogs(
            @PathVariable String targetType,
            @PathVariable String targetId
    ) {
        return adminManagementService.getActionLogs(targetType, targetId);
    }

    @GetMapping("/audit-logs/{entityType}/{entityId}")
    public List<AuditLogResponse> getAuditLogs(
            @PathVariable String entityType,
            @PathVariable String entityId
    ) {
        return adminManagementService.getAuditLogs(entityType, entityId);
    }
}