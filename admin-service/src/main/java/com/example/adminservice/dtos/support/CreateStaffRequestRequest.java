package com.example.adminservice.dtos.support;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStaffRequestRequest(
        @NotBlank(message = "Category is required")
        @Size(max = 40, message = "Category must be 40 characters or fewer")
        String category,

        @NotBlank(message = "Subject is required")
        @Size(max = 120, message = "Subject must be 120 characters or fewer")
        String subject,

        @NotBlank(message = "Message is required")
        @Size(max = 2000, message = "Message must be 2000 characters or fewer")
        String message,

        @Size(max = 40, message = "Reference type must be 40 characters or fewer")
        String referenceType,

        @Size(max = 120, message = "Reference ID must be 120 characters or fewer")
        String referenceId,

        @Size(max = 255, message = "Context path must be 255 characters or fewer")
        String contextPath
) {
}
