package com.locally.orders.order_reservationservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data // for getter and setters
@Builder // build the complex objects later
public class Order {

    @Id
    private String id;

    private String shopperId;
    private String resturantId;
    private List<String> items; // list of product idss
    private Double totalPrice;
    private String status;
    private LocalDateTime orderDate;
    private List<StatusHistory> history;


}