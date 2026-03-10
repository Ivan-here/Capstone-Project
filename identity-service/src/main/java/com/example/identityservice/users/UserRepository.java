package com.example.identityservice.users;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<UserCredential, String> {
    Optional<UserCredential> findByEmailOrUsername(String email, String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String normalizedUsername);

    List<UserCredential> findByStatus(UserStatus status);

    List<UserCredential> findByRolesContaining(Role role);

    @Query("{ '$or': [ " +
            "{ 'email': { $regex: ?0, $options: 'i' } }, " +
            "{ 'username': { $regex: ?0, $options: 'i' } }, " +
            "{ 'firstName': { $regex: ?0, $options: 'i' } }, " +
            "{ 'lastName': { $regex: ?0, $options: 'i' } }, " +
            "{ 'displayName': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<UserCredential> searchUsers(String normalizedQuery);
}