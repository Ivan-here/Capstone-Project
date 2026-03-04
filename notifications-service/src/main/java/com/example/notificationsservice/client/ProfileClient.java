package com.example.notificationsservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class ProfileClient {

    private final WebClient webClient;
    private final String profileBaseUrl;

    public ProfileClient(WebClient webClient,
                         @Value("${services.profile.base-url}") String profileBaseUrl) {
        this.webClient = webClient;
        this.profileBaseUrl = profileBaseUrl;
    }

    /**
     * Returns true if profile-service confirms this user exists.
     * Adjust the URI path to match YOUR profile-service endpoint.
     */
    public boolean userExists(String userId) {
        try {
            // CHANGE THIS PATH if your profile-service uses a different route:
            // e.g. /profiles/{userId}, /profile/{userId}, /users/{userId}, etc.
            webClient.get()
                    .uri(profileBaseUrl + "/internal/profiles/" + userId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return true;

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) return false;
            throw e; // 500, 401, service down, etc.
        }
    }
}