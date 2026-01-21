package com.locally.orders.order_reservationservice.repository;

import com.locally.orders.order_reservationservice.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    List<Reservation> findByNgoId(String ngoId);
    List<Reservation> findByRestaurantId(String restaurantId);
}