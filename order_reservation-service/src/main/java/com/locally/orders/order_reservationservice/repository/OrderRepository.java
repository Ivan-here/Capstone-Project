package com.locally.orders.order_reservationservice.repository;

import com.locally.orders.order_reservationservice.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String>{

    List<Order> findByShopperId(String shopperId);
    List<Order> findByStatus(String status);

}
