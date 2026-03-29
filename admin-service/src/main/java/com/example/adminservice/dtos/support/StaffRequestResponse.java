package com.example.adminservice.dtos.support;

public record StaffRequestResponse(
        String referenceId,
        int recipients,
        String message
) {
}
