package com.example.adminservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateModerationActionRequest {
    private String targetType;
    private String targetId;
    private String action;
    private String reason;
    private String notes;
}