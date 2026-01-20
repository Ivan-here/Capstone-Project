package com.example.profileservice.profiles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleUpgradeClient {

    private final IdentityClient identityClient;

    public void addRoleToUser(String userId, String role) {
        log.info("Requesting role upgrade via Feign: userId={}, role={}", userId, role);
        identityClient.addRole(userId, Map.of("role", role));
    }
}
//public class RoleUpgradeClient {
//
//    private final WebClient webClient;
//    private final String internalSecret;
//    private final String identityBaseUrl;
//
//    public RoleUpgradeClient(
//            WebClient.Builder builder,
//            @Value("${clients.identityBaseUrl}") String identityBaseUrl,
//            @Value("${internal.sharedSecret}") String internalSecret
//    ) {
//        this.webClient = builder.build();
//        this.identityBaseUrl = identityBaseUrl;
//        this.internalSecret = internalSecret;
//    }
//
//    public void addRoleToUser(String userId, String role) {
//        String url = identityBaseUrl + "/internal/users/" + userId + "/roles:add";
//
//        log.info("Requesting role upgrade: userId={}, role={}", userId, role);
//
//        webClient.post()
//                .uri(url)
//                .contentType(MediaType.APPLICATION_JSON)
//                .header("X-Internal-Secret", internalSecret)
//                .bodyValue(Map.of("role", role))
//                .retrieve()
//                .toBodilessEntity()
//                .doOnError(e -> log.warn("Role upgrade call failed: {}", e.getMessage()))
//                .block();
//    }
//}