package com.example.identityservice.users;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserCredential, String> {
    Optional<UserCredential> findByEmail(String email);
    boolean existsByEmail(String email);
}