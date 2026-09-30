package com.locally.orders.order_reservationservice.security;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.bson.Document;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.UUID;

/** Serializes payment/stock transitions across replicas. Crashed operations require reconciliation, not an unsafe lease expiry. */
@Aspect
@Component
@RequiredArgsConstructor
public class OrderMutationLock {
    private final MongoTemplate mongo;

    @Around("execution(public * com.locally.orders.order_reservationservice.service.OrderReservationService.*(..)) && args(id,..)")
    public Object serialize(ProceedingJoinPoint join, String id) throws Throwable {
        String method = join.getSignature().getName();
        if (!java.util.Set.of("createPaymentIntentForOrder", "cancelOrder", "adminDisputeOrder", "markPaymentSucceeded",
                "confirmPaymentForShopper", "confirmDonationForShopper", "markReadyForPickup", "verifyPickupCode",
                "deleteOrder", "updateReservation", "updateReservationStatus", "deleteReservation").contains(method)) return join.proceed();
        String key = (method.contains("Reservation") ? "reservation:" : "order:") + id;
        String token = UUID.randomUUID().toString();
        try {
            mongo.insert(new Document("_id", key).append("token", token).append("startedAt", Instant.now().toString()), "operation_locks");
        } catch (DuplicateKeyException busy) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Operation already in progress");
        }
        try { return join.proceed(); }
        finally { mongo.remove(Query.query(Criteria.where("_id").is(key).and("token").is(token)), "operation_locks"); }
    }
}
