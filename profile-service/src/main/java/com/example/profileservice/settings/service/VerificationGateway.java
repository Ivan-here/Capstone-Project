package com.example.profileservice.settings.service;

import com.example.profileservice.settings.dto.SubmitVerificationRequest;
import com.example.profileservice.settings.dto.VerificationRecordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationGateway {

    private final WebClient webClient;

    @Value("${clients.verificationBaseUrl}")
    private String verificationBaseUrl;

    public VerificationRecordResponse getLatestByUserId(String userId) {
        try {
            return webClient.get()
                    .uri(verificationBaseUrl + "/api/verification/user/{userId}", userId)
                    .retrieve()
                    .bodyToMono(VerificationRecordResponse.class)
                    .block();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<VerificationRecordResponse> getByUserId(String userId) {
        try {
            List<VerificationRecordResponse> records = webClient.get()
                    .uri(verificationBaseUrl + "/api/verification/admin/user/{userId}", userId)
                    .retrieve()
                    .bodyToFlux(VerificationRecordResponse.class)
                    .collectList()
                    .block();
            return records == null ? List.of() : records;
        } catch (Exception ex) {
            log.warn("Failed to load verification records for userId={}", userId, ex);
            return List.of();
        }
    }

    public VerificationRecordResponse submit(String userId, String type, MultipartFile document) {
        try {
            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("request", new SubmitVerificationRequest(userId, type), MediaType.APPLICATION_JSON);
            bodyBuilder.part("document", document.getResource())
                    .filename(document.getOriginalFilename() == null ? "verification-document" : document.getOriginalFilename());

            return webClient.post()
                    .uri(verificationBaseUrl + "/api/verification/submit")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve()
                    .onStatus(status -> status.value() == HttpStatus.CONFLICT.value(), response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("Verification request conflict")
                                    .map(message -> new ResponseStatusException(HttpStatus.CONFLICT, message)))
                    .onStatus(status -> status.value() == HttpStatus.BAD_REQUEST.value(), response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("Invalid verification request")
                                    .map(message -> new ResponseStatusException(HttpStatus.BAD_REQUEST, message)))
                    .bodyToMono(VerificationRecordResponse.class)
                    .block();
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to submit verification request");
        }
    }

    public void deleteAllByUserId(String userId) {
        List<VerificationRecordResponse> existing = getByUserId(userId);
        List<String> failedIds = new ArrayList<>();

        for (VerificationRecordResponse record : existing) {
            try {
                webClient.delete()
                        .uri(verificationBaseUrl + "/api/verification/admin/{id}", record.id())
                        .retrieve()
                        .toBodilessEntity()
                        .block();
            } catch (Exception ex) {
                failedIds.add(record.id());
                log.warn("Failed to delete verification request id={} userId={}", record.id(), userId, ex);
            }
        }

        if (!failedIds.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Failed to delete some verification records: " + String.join(", ", failedIds)
            );
        }
    }
}
