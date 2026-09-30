package com.example.communityservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class FollowVisibilityClient {
    private final WebClient client;
    private final String baseUrl;
    public FollowVisibilityClient(WebClient client, @Value("${services.follow.base-url:http://localhost:8091}") String baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }
    public boolean follows(String follower, String author) {
        return Boolean.TRUE.equals(client.get().uri(baseUrl + "/api/follows/internal/relationship?follower={f}&author={a}", follower, author)
                .retrieve().bodyToMono(Boolean.class).block(java.time.Duration.ofSeconds(5)));
    }
}
