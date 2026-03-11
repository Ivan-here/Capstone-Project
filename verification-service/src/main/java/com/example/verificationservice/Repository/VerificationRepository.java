package com.example.verificationservice.Repository;

import com.example.verificationservice.Model.Verification;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface VerificationRepository extends MongoRepository<Verification, String> {
    // Custom query to find requests by their status (PENDING, APPROVED, etc.)
    List<Verification> findByStatus(String status);

    // ADD THIS NEW LINE:
    List<Verification> findAllByUserId(String userId);
}