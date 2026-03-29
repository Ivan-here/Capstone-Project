package com.example.adminservice.service;

import com.example.adminservice.dtos.notification.CreateNotificationRequest;
import com.example.adminservice.dtos.support.CreateStaffRequestRequest;
import com.example.adminservice.dtos.support.StaffRequestResponse;
import com.example.adminservice.dtos.user.UserCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
public class StaffRequestService {

    private static final String NOTIFICATION_TYPE = "STAFF_REQUEST";

    private final AdminUserService adminUserService;
    private final AdminNotificationService adminNotificationService;
    private final AdminManagementService adminManagementService;

    public StaffRequestResponse create(String userId, CreateStaffRequestRequest request) {
        List<UserCredential> admins = adminUserService.getUsersByRole("ADMIN");

        if (admins == null || admins.isEmpty()) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "No admin recipients are available");
        }

        String category = request.category().trim().toUpperCase();
        String subject = request.subject().trim();
        String message = request.message().trim();
        String referenceId = UUID.randomUUID().toString();
        String contextPath = normalizePath(request.contextPath());
        String referenceType = normalizeUpper(request.referenceType());
        String referenceTargetId = normalizePath(request.referenceId());
        String title = "Staff request: " + categoryLabel(category);
        String body = buildBody(subject, message, referenceType, referenceTargetId, contextPath);

        for (UserCredential admin : admins) {
            adminNotificationService.createNotification(new CreateNotificationRequest(
                    admin.getId(),
                    userId,
                    NOTIFICATION_TYPE,
                    title,
                    body,
                    "admin-service",
                    referenceType == null ? "STAFF_REQUEST" : referenceType,
                    referenceTargetId == null ? referenceId : referenceTargetId,
                    "ORDER".equals(referenceType) ? "/admin/orders" : "/admin/notifications"
            ));
        }

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("category", category);
        metadata.put("subject", subject);
        metadata.put("referenceType", referenceType);
        metadata.put("referenceId", referenceTargetId);
        metadata.put("contextPath", contextPath);
        metadata.put("recipients", admins.stream().map(UserCredential::getId).toList());

        adminManagementService.logAudit(
                "STAFF_REQUEST_CREATED",
                "STAFF_REQUEST",
                referenceId,
                userId,
                "Staff request submitted: " + subject,
                metadata
        );

        return new StaffRequestResponse(
                referenceId,
                admins.size(),
                "Staff request submitted successfully"
        );
    }

    private String buildBody(String subject, String message, String referenceType, String referenceId, String contextPath) {
        StringBuilder body = new StringBuilder()
                .append("Subject: ").append(subject)
                .append("\n\n")
                .append(message);

        if (referenceType != null && referenceId != null) {
            body.append("\n\nReference: ").append(referenceType).append(" #").append(referenceId);
        }

        if (contextPath != null) {
            body.append("\n\nContext: ").append(contextPath);
        }

        return body.toString();
    }

    private String normalizePath(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String normalizeUpper(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase();
    }

    private String categoryLabel(String category) {
        return switch (category) {
            case "BUG" -> "Bug report";
            case "HELP" -> "Help request";
            case "ACCOUNT" -> "Account issue";
            case "VERIFICATION" -> "Verification support";
            case "ORDER_DISPUTE" -> "Order dispute";
            default -> category.charAt(0) + category.substring(1).toLowerCase();
        };
    }
}
