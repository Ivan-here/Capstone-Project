package com.locally.orders.order_reservationservice.repository;

import com.locally.orders.order_reservationservice.model.Order;
import com.locally.orders.order_reservationservice.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String>{

    List<Order> findByShopperId(String shopperId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByRestaurantId(String restaurantId);



}
