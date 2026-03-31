package com.example.followservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "follows")
public class Follow {
    @Id
    private String id;
    private String followerId;
    private String followingId;
    private ConnectionStatus status;
    private LocalDateTime createdAt;

    public Follow(String followerId, String followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.status = ConnectionStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }
}