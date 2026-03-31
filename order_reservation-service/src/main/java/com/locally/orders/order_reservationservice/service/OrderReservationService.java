package com.locally.orders.order_reservationservice.service;

import com.locally.orders.order_reservationservice.client.ListingClient;
import com.locally.orders.order_reservationservice.dtos.*;
import com.locally.orders.order_reservationservice.model.Order;
import com.locally.orders.order_reservationservice.model.OrderItem;
import com.locally.orders.order_reservationservice.model.OrderStatus;
import com.locally.orders.order_reservationservice.model.PaymentStatus;
import com.locally.orders.order_reservationservice.model.Reservation;
import com.locally.orders.order_reservationservice.repository.OrderRepository;
import com.locally.orders.order_reservationservice.repository.ReservationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.locally.orders.order_reservationservice.client.PaymentClient;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class OrderReservationService {

    private static final String ORDER_CURRENCY = "cad";
    private final PasswordEncoder passwordEncoder;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final ListingClient listingClient;
    private final PaymentClient paymentClient;

    @Value("${platform.fee-percent}")
    private double platformFeePercent;

    @PostConstruct
    public void validatePlatformFeePercent() {
        if (platformFeePercent < 0 || platformFeePercent > 100) {
            throw new IllegalStateException("platform.fee-percent must be between 0 and 100");
        }
    }

    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        validateCreateOrderRequest(request);

        List<OrderItem> orderItems = new ArrayList<>();
        String sellerUserId = null;
        String pickupLocation = null;

        for (var requestItem : request.items()) {
            ListingClient.ListingDto listing = listingClient.getListing(requestItem.listingId());

            if (listing == null) {
                throw new RuntimeException("Listing not found: " + requestItem.listingId());
            }

            validateListingForOrder(listing, requestItem.quantity());

            if (sellerUserId == null) {
                sellerUserId = listing.getOwnerId();
                pickupLocation = listing.getPickupLocation();
            } else if (!sellerUserId.equals(listing.getOwnerId())) {
                throw new RuntimeException("All items in one order must belong to the same seller");
            }

            long unitPriceCents = toCents(listing.getPrice());
            long lineTotalCents = unitPriceCents * requestItem.quantity();

            OrderItem orderItem = OrderItem.builder()
                    .listingId(listing.getId())
                    .title(listing.getTitle())
                    .unit(listing.getUnit())
                    .unitPriceCents(unitPriceCents)
                    .quantity(requestItem.quantity())
                    .lineTotalCents(lineTotalCents)
                    .build();

            orderItems.add(orderItem);
        }

        long grossAmountCents = orderItems.stream()
                .mapToLong(OrderItem::getLineTotalCents)
                .sum();

        long platformFeeCents = calculatePlatformFeeCents(grossAmountCents);
        long sellerAmountCents = grossAmountCents - platformFeeCents;

        Order order = Order.builder()
                .shopperId(request.shopperId())
                .sellerUserId(sellerUserId)
                .items(orderItems)
                .pickupLocation(pickupLocation)
                .currency(ORDER_CURRENCY)
                .grossAmountCents(grossAmountCents)
                .platformFeeCents(platformFeeCents)
                .sellerAmountCents(sellerAmountCents)
                .status(OrderStatus.PENDING_PAYMENT)
                .paymentStatus(PaymentStatus.REQUIRES_PAYMENT)
                .stockDeducted(false)
                .pickupCodeVerified(false)
                .build();

        order.updateStatus(OrderStatus.PENDING_PAYMENT, request.shopperId());

        Order saved = orderRepository.save(order);

        return new CreateOrderResponse(
                saved.getId(),
                saved.getStatus().name(),
                saved.getPaymentStatus().name(),
                saved.getGrossAmountCents(),
                saved.getCurrency()
        );
    }

    public OrderPaymentIntentResponse createPaymentIntentForOrder(String orderId, String shopperId) {
        Order order = getOrderById(orderId);

        if (!order.getShopperId().equals(shopperId)) {
            throw new RuntimeException("Only the shopper who created the order can start payment");
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Payment can only be started for PENDING_PAYMENT orders");
        }

        if (order.getPaymentStatus() != PaymentStatus.REQUIRES_PAYMENT) {
            throw new RuntimeException("Order is not in a payable state");
        }

        if (order.getGrossAmountCents() == null || order.getGrossAmountCents() <= 0) {
            throw new RuntimeException("Order amount is invalid");
        }

        if (order.getSellerUserId() == null || order.getSellerUserId().isBlank()) {
            throw new RuntimeException("Order seller is missing");
        }

        CreatePaymentIntentRequest request = new CreatePaymentIntentRequest(
                order.getId(),
                order.getSellerUserId(),
                order.getGrossAmountCents(),
                order.getPlatformFeeCents(),
                order.getSellerAmountCents(),
                order.getCurrency()
        );

        CreatePaymentIntentResponse response = paymentClient.createPaymentIntent(request);

        if (response == null) {
            throw new RuntimeException("Payment service did not return a response");
        }

        order.setStripePaymentIntentId(response.paymentIntentId());
        order.setStripeClientSecret(response.clientSecret());

        orderRepository.save(order);

        return new OrderPaymentIntentResponse(
                order.getId(),
                response.paymentIntentId(),
                response.clientSecret(),
                order.getPaymentStatus().name()
        );
    }

    public Order cancelOrder(String orderId, CancelOrderRequest request) {
        Order order = getOrderById(orderId);

        if (request == null || request.actorUserId() == null || request.actorUserId().isBlank()) {
            throw new RuntimeException("actorUserId is required");
        }

        boolean isShopper = request.actorUserId().equals(order.getShopperId());
        boolean isSeller = request.actorUserId().equals(order.getSellerUserId());

        if (!isShopper && !isSeller) {
            throw new RuntimeException("Only the shopper or seller can cancel this order");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return order;
        }

        if (order.getPaymentStatus() == PaymentStatus.RELEASED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Completed or released orders cannot be cancelled normally");
        }

        if (order.getStatus() == OrderStatus.PENDING_PAYMENT
                && order.getPaymentStatus() == PaymentStatus.REQUIRES_PAYMENT) {

            order.setCancelledAt(java.time.LocalDateTime.now());
            order.setPaymentStatus(PaymentStatus.CANCELLED);
            order.updateStatus(OrderStatus.CANCELLED, request.actorUserId());

            return orderRepository.save(order);
        }

        if ((order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.READY_FOR_PICKUP)
                && order.getPaymentStatus() == PaymentStatus.HELD) {

            RefundPaymentResponse refundResponse = paymentClient.refundPayment(
                    new RefundPaymentRequest(
                            order.getId(),
                            order.getStripePaymentIntentId(),
                            order.getGrossAmountCents(),
                            request.reason()
                    )
            );

            if (refundResponse == null || refundResponse.refundId() == null || refundResponse.refundId().isBlank()) {
                throw new RuntimeException("Refund failed");
            }

            if (order.isStockDeducted()) {
                restoreOrderStock(order);
                order.setStockDeducted(false);
            }

            order.setStripeRefundId(refundResponse.refundId());
            order.setRefundedAt(java.time.LocalDateTime.now());
            order.setCancelledAt(java.time.LocalDateTime.now());
            order.setPaymentStatus(PaymentStatus.REFUNDED);
            order.updateStatus(OrderStatus.CANCELLED, request.actorUserId());

            return orderRepository.save(order);
        }

        throw new RuntimeException("Order cannot be cancelled in its current state");
    }

    public Order adminDisputeOrder(String orderId, AdminDisputeOrderRequest request) {
        Order order = getOrderById(orderId);

        if (request == null || request.adminUserId() == null || request.adminUserId().isBlank()) {
            throw new RuntimeException("adminUserId is required");
        }

        if (request.reason() == null || request.reason().isBlank()) {
            throw new RuntimeException("reason is required");
        }

        order.updateStatus(OrderStatus.DISPUTED, request.adminUserId());

        if (!request.refundPayment()) {
            return orderRepository.save(order);
        }

        if (order.getPaymentStatus() != PaymentStatus.HELD) {
            throw new RuntimeException("Refund is only available while funds are still held");
        }

        if (order.getStripePaymentIntentId() == null || order.getStripePaymentIntentId().isBlank()) {
            throw new RuntimeException("Order payment intent is missing");
        }

        RefundPaymentResponse refundResponse = paymentClient.refundPayment(
                new RefundPaymentRequest(
                        order.getId(),
                        order.getStripePaymentIntentId(),
                        order.getGrossAmountCents(),
                        "admin_dispute: " + request.reason().trim()
                )
        );

        if (refundResponse == null || refundResponse.refundId() == null || refundResponse.refundId().isBlank()) {
            throw new RuntimeException("Refund failed");
        }

        if (order.isStockDeducted()) {
            restoreOrderStock(order);
            order.setStockDeducted(false);
        }

        order.setStripeRefundId(refundResponse.refundId());
        order.setRefundedAt(LocalDateTime.now());
        order.setCancelledAt(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.REFUNDED);
        order.updateStatus(OrderStatus.CANCELLED, request.adminUserId());

        return orderRepository.save(order);
    }

    public PaymentSucceededResponse markPaymentSucceeded(String orderId, PaymentSucceededRequest request) {
        Order order = getOrderById(orderId);

        if (request == null || request.paymentIntentId() == null || request.paymentIntentId().isBlank()) {
            throw new RuntimeException("paymentIntentId is required");
        }

        //if already handled, return current state
        if (order.getPaymentStatus() == PaymentStatus.HELD || order.getStatus() == OrderStatus.PAID) {
            return new PaymentSucceededResponse(
                    order.getId(),
                    order.getStatus().name(),
                    order.getPaymentStatus().name(),
                    order.isStockDeducted()
            );
        }

        if (order.getStripePaymentIntentId() == null || !order.getStripePaymentIntentId().equals(request.paymentIntentId())) {
            throw new RuntimeException("Payment intent does not match order");
        }

        if (order.getPaymentStatus() != PaymentStatus.PAYMENT_PROCESSING
                && order.getPaymentStatus() != PaymentStatus.REQUIRES_PAYMENT) {
            throw new RuntimeException("Order is not in a payment-confirmable state");
        }

        if (!order.isStockDeducted()) {
            for (OrderItem item : order.getItems()) {
                ListingClient.ListingDto listing = listingClient.getListing(item.getListingId());

                if (listing == null) {
                    throw new RuntimeException("Listing not found during stock deduction: " + item.getListingId());
                }

                int currentQty = listing.getQuantity() == null ? 0 : listing.getQuantity();
                if (currentQty < item.getQuantity()) {
                    throw new RuntimeException("Not enough stock to finalize payment for listing " + item.getListingId());
                }

                int newQty = currentQty - item.getQuantity();
                listingClient.updateListingQuantity(item.getListingId(), newQty);
            }

            order.setStockDeducted(true);
        }

        String rawPickupCode = generatePickupCode();

        order.setPickupCodeHash(passwordEncoder.encode(rawPickupCode));
        order.setPickupCodePlain(rawPickupCode);
        order.setPickupCodeExpiresAt(LocalDateTime.now().plusDays(7));
        order.setPickupCodeVerified(false);
        order.setPaidAt(LocalDateTime.now());

        order.setPaymentStatus(PaymentStatus.HELD);
        order.updateStatus(OrderStatus.PAID, "system");

        orderRepository.save(order);

        return new PaymentSucceededResponse(
                order.getId(),
                order.getStatus().name(),
                order.getPaymentStatus().name(),
                order.isStockDeducted()
        );
    }

    public PaymentSucceededResponse confirmPaymentForShopper(String orderId, String shopperId, PaymentSucceededRequest request) {
        Order order = getOrderById(orderId);

        if (shopperId == null || shopperId.isBlank()) {
            throw new RuntimeException("shopperId is required");
        }

        if (!shopperId.equals(order.getShopperId())) {
            throw new RuntimeException("Only the shopper who created the order can confirm payment");
        }

        return markPaymentSucceeded(orderId, request);
    }
    public Order markReadyForPickup(String orderId, ReadyForPickupRequest request) {
        Order order = getOrderById(orderId);

        if (request == null || request.sellerUserId() == null || request.sellerUserId().isBlank()) {
            throw new RuntimeException("sellerUserId is required");
        }

        if (!request.sellerUserId().equals(order.getSellerUserId())) {
            throw new RuntimeException("Only the seller of this order can mark it ready");
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Only PAID orders can be marked ready for pickup");
        }

        if (order.getPaymentStatus() != PaymentStatus.HELD) {
            throw new RuntimeException("Order payment must be HELD before pickup");
        }

        order.setReadyForPickupAt(java.time.LocalDateTime.now());
        order.updateStatus(OrderStatus.READY_FOR_PICKUP, request.sellerUserId());

        return orderRepository.save(order);
    }

    public Order verifyPickupCode(String orderId, VerifyPickupCodeRequest request) {
        Order order = getOrderById(orderId);

        if (request == null || request.sellerUserId() == null || request.sellerUserId().isBlank()) {
            throw new RuntimeException("sellerUserId is required");
        }

        if (request.code() == null || request.code().isBlank()) {
            throw new RuntimeException("Pickup code is required");
        }

        if (!request.sellerUserId().equals(order.getSellerUserId())) {
            throw new RuntimeException("Only the seller of this order can verify pickup");
        }

        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new RuntimeException("Order is not ready for pickup");
        }

        if (order.getPaymentStatus() != PaymentStatus.HELD) {
            throw new RuntimeException("Payment must be HELD before pickup verification");
        }

        if (order.getPickupCodeHash() == null || order.getPickupCodeHash().isBlank()) {
            throw new RuntimeException("Pickup code is not set for this order");
        }

        if (Boolean.TRUE.equals(order.getPickupCodeVerified())) {
            throw new RuntimeException("Pickup code already verified");
        }

        if (order.getPickupCodeExpiresAt() != null &&
                java.time.LocalDateTime.now().isAfter(order.getPickupCodeExpiresAt())) {
            throw new RuntimeException("Pickup code has expired");
        }

        boolean matches = passwordEncoder.matches(request.code(), order.getPickupCodeHash());
        if (!matches) {
            throw new RuntimeException("Invalid pickup code");
        }

        order.setPickupCodeVerified(true);
        order.setPickupVerifiedAt(java.time.LocalDateTime.now());
        order.setPickupVerifiedBy(request.sellerUserId());
        order.setPickupCodePlain(null);
        order.updateStatus(OrderStatus.PICKUP_CODE_VERIFIED, request.sellerUserId());
        order.setCompletedAt(java.time.LocalDateTime.now());
        order.updateStatus(OrderStatus.COMPLETED, request.sellerUserId());
        order.setPaymentStatus(PaymentStatus.RELEASE_PENDING);

        order = orderRepository.save(order);

        ReleaseFundsResponse releaseResponse = paymentClient.releaseFunds(
                new ReleaseFundsRequest(
                        order.getId(),
                        order.getSellerUserId(),
                        order.getSellerAmountCents(),
                        order.getCurrency()
                )
        );

        if (releaseResponse == null || releaseResponse.transferId() == null || releaseResponse.transferId().isBlank()) {
            throw new RuntimeException("Funds release failed");
        }

        order.setStripeTransferId(releaseResponse.transferId());
        order.setReleasedAt(java.time.LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.RELEASED);

        return orderRepository.save(order);
    }

    private String generatePickupCode() {
        int code = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(code);
    }

    public PickupCodeResponse getPickupCode(String orderId, String shopperId) {
        Order order = getOrderById(orderId);

        if (order.getPickupCodeExpiresAt() != null &&
                LocalDateTime.now().isAfter(order.getPickupCodeExpiresAt())) {
            throw new RuntimeException("Pickup code expired");
        }

        if (!order.getShopperId().equals(shopperId)) {
            throw new RuntimeException("Only the buyer can view the pickup code");
        }

        if (order.getPickupCodePlain() == null) {
            throw new RuntimeException("Pickup code is not available");
        }

        return new PickupCodeResponse(
                order.getId(),
                order.getPickupCodePlain()
        );
    }

    private void validateCreateOrderRequest(CreateOrderRequest request) {
        if (request == null) {
            throw new RuntimeException("Request body is required");
        }

        if (request.shopperId() == null || request.shopperId().isBlank()) {
            throw new RuntimeException("shopperId is required");
        }

        if (request.items() == null || request.items().isEmpty()) {
            throw new RuntimeException("At least one item is required");
        }

        Set<String> seenListingIds = new HashSet<>();
        for (var item : request.items()) {
            if (item == null) {
                throw new RuntimeException("Order item cannot be null");
            }

            if (item.listingId() == null || item.listingId().isBlank()) {
                throw new RuntimeException("listingId is required for every item");
            }

            if (item.quantity() == null || item.quantity() <= 0) {
                throw new RuntimeException("Quantity must be greater than 0 for listing " + item.listingId());
            }

            if (!seenListingIds.add(item.listingId())) {
                throw new RuntimeException("Duplicate listing in request: " + item.listingId());
            }
        }
    }

    private void validateListingForOrder(ListingClient.ListingDto listing, Integer requestedQty) {
        if (listing.getStatus() == null || !"ACTIVE".equalsIgnoreCase(listing.getStatus())) {
            throw new RuntimeException("Listing is not available: " + listing.getId());
        }

        if (listing.getType() == null || !"FARM_PRODUCT".equalsIgnoreCase(listing.getType())) {
            throw new RuntimeException("Only FARM_PRODUCT listings can be ordered");
        }

        int availableQty = listing.getQuantity() == null ? 0 : listing.getQuantity();
        if (availableQty < requestedQty) {
            throw new RuntimeException("Not enough stock for listing " + listing.getId() +
                    " (wanted=" + requestedQty + ", available=" + availableQty + ")");
        }

        if (listing.getOwnerId() == null || listing.getOwnerId().isBlank()) {
            throw new RuntimeException("Listing owner is missing for listing " + listing.getId());
        }

        if (listing.getPrice() == null) {
            throw new RuntimeException("Listing price is missing for listing " + listing.getId());
        }
    }

    private long toCents(BigDecimal amount) {
        return amount.movePointRight(2).longValueExact();
    }

    private long calculatePlatformFeeCents(long grossAmountCents) {
        return BigDecimal.valueOf(grossAmountCents)
                .multiply(BigDecimal.valueOf(platformFeePercent))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    private void restoreOrderStock(Order order) {
        if (order.getItems() == null) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            ListingClient.ListingDto listing = listingClient.getListing(item.getListingId());
            if (listing == null) {
                throw new RuntimeException("Listing not found during stock restore: " + item.getListingId());
            }

            int currentQty = listing.getQuantity() == null ? 0 : listing.getQuantity();
            int newQty = currentQty + item.getQuantity();
            listingClient.updateListingQuantity(item.getListingId(), newQty);
        }
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void deleteOrder(String orderId) {
        Order existing = getOrderById(orderId);
        orderRepository.delete(existing);
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getOrdersByShopper(String shopperId) {
        return orderRepository.findByShopperId(shopperId).stream()
                .filter(this::shouldIncludeInHistory)
                .toList();
    }

    public List<Order> getOrdersBySeller(String sellerUserId) {
        return orderRepository.findBySellerUserId(sellerUserId).stream()
                .filter(this::shouldIncludeInHistory)
                .toList();
    }

    public OrderHistoryResponse getOrderHistory(String userId) {
        List<Order> bought = orderRepository.findByShopperId(userId).stream()
                .filter(this::shouldIncludeInHistory)
                .sorted(Comparator.comparing(Order::getOrderDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();

        List<Order> sold = orderRepository.findBySellerUserId(userId).stream()
                .filter(this::shouldIncludeInHistory)
                .sorted(Comparator.comparing(Order::getOrderDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();

        return new OrderHistoryResponse(bought, sold);
    }

    private boolean shouldIncludeInHistory(Order order) {
        if (order == null) {
            return false;
        }

        if (order.getPaymentStatus() == PaymentStatus.REQUIRES_PAYMENT) {
            return false;
        }

        if (order.getStatus() == OrderStatus.CANCELLED
                && order.getPaymentStatus() == PaymentStatus.CANCELLED
                && order.getPaidAt() == null
                && order.getRefundedAt() == null) {
            return false;
        }

        return true;
    }

    // ================= RESERVATIONS =================

    public Reservation createReservation(Reservation reservation) {
        var listing = listingClient.getListing(reservation.getSurplusItemId());
        if (listing == null) {
            throw new RuntimeException("Listing not found");
        }

        if (listing.getStatus() == null || !"ACTIVE".equalsIgnoreCase(listing.getStatus())) {
            throw new RuntimeException("Cannot reserve: listing is not ACTIVE (status=" + listing.getStatus() + ")");
        }
        if (listing.getType() == null || !"SURPLUS_FOOD".equalsIgnoreCase(listing.getType())) {
            throw new RuntimeException("Cannot reserve: listing type must be SURPLUS_FOOD (type=" + listing.getType() + ")");
        }

        int availableQty = listing.getQuantity() == null ? 0 : listing.getQuantity();
        if (availableQty < 1) {
            throw new RuntimeException("No quantity available");
        }

        listingClient.updateListingQuantity(reservation.getSurplusItemId(), availableQty - 1);

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByNgo(String ngoId) {
        return reservationRepository.findByNgoId(ngoId);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public Reservation updateReservation(String id, Reservation updatedReservation) {
        Reservation existing = getReservationById(id);

        existing.setNgoId(updatedReservation.getNgoId());
        existing.setSurplusItemId(updatedReservation.getSurplusItemId());

        if (updatedReservation.getStatus() != null && !updatedReservation.getStatus().isBlank()) {
            if ("CANCELLED".equalsIgnoreCase(updatedReservation.getStatus())
                    && !"CANCELLED".equalsIgnoreCase(existing.getStatus())) {
                var listing = listingClient.getListing(existing.getSurplusItemId());
                if (listing != null) {
                    int currentQty = listing.getQuantity() == null ? 0 : listing.getQuantity();
                    listingClient.updateListingQuantity(existing.getSurplusItemId(), currentQty + 1);
                }
            }

            existing.setStatus(updatedReservation.getStatus());
        }

        return reservationRepository.save(existing);
    }

    public Reservation updateReservationStatus(String id, String status) {
        Reservation reservation = getReservationById(id);

        if ("CANCELLED".equalsIgnoreCase(status) && !"CANCELLED".equalsIgnoreCase(reservation.getStatus())) {
            var listing = listingClient.getListing(reservation.getSurplusItemId());
            if (listing != null) {
                int currentQty = listing.getQuantity() == null ? 0 : listing.getQuantity();
                listingClient.updateListingQuantity(reservation.getSurplusItemId(), currentQty + 1);
            }
        }

        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }

    public void deleteReservation(String id) {
        Reservation existing = getReservationById(id);

        if (!"CANCELLED".equalsIgnoreCase(existing.getStatus())) {
            var listing = listingClient.getListing(existing.getSurplusItemId());
            if (listing != null) {
                int currentQty = listing.getQuantity() == null ? 0 : listing.getQuantity();
                listingClient.updateListingQuantity(existing.getSurplusItemId(), currentQty + 1);
            }
        }

        reservationRepository.delete(existing);
    }
}
