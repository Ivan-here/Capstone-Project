package com.locally.orders.order_reservationservice.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ListingClient {

    private final RestTemplate restTemplate;

    @Value("${listing.service.base-url:http://localhost:8084}")
    private String listingBaseUrl;

    public ListingDto getListing(String listingId) {
        try {
            return restTemplate.getForObject(
                    listingBaseUrl + "/api/listings/" + listingId,
                    ListingDto.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    public void updateListingQuantity(String listingId, int newQuantity) {
        UpdateQuantityDto body = new UpdateQuantityDto(newQuantity);

        restTemplate.exchange(
                listingBaseUrl + "/api/listings/" + listingId + "/quantity",
                HttpMethod.PATCH,
                new HttpEntity<>(body),
                Void.class
        );
    }

    @Data
    public static class ListingDto {
        private String id;
        private String ownerId;
        private String title;
        private String businessName;
        private String pickupLocation;
        private Integer quantity;
        private String status;
        private String type;
        private BigDecimal price;
        private String unit;
    }

    public record UpdateQuantityDto(Integer newQuantity) {}
}