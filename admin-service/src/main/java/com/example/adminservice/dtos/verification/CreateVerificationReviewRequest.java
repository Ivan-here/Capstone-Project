package com.example.adminservice.dtos.verification;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVerificationReviewRequest {
    private String verificationId;
    private String userId;
    private String type;
    private String documentUrl;
    private String requestedBy;
}
