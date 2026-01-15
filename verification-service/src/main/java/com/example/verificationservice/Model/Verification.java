package com.example.verificationservice.Model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(value = "verification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Verification {

    @Id
    private String id;
    private String userId;
    private String type; // e.g., FARMER, RESTAURANT, NGO
    private String status; // PENDING, APPROVED, REJECTED
    private String documentUrl; // Link to uploaded verification docs
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
