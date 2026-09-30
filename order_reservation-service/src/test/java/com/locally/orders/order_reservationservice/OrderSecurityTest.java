package com.locally.orders.order_reservationservice;

import com.locally.orders.order_reservationservice.client.*;
import com.locally.orders.order_reservationservice.dtos.*;
import com.locally.orders.order_reservationservice.model.*;
import com.locally.orders.order_reservationservice.model.Order;
import com.locally.orders.order_reservationservice.repository.*;
import com.locally.orders.order_reservationservice.service.OrderReservationService;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderSecurityTest {
    OrderRepository orders = mock(OrderRepository.class);
    ReservationRepository reservations = mock(ReservationRepository.class);
    ListingClient listings = mock(ListingClient.class);
    PaymentClient payments = mock(PaymentClient.class);
    OrderReservationService service = new OrderReservationService(mock(PasswordEncoder.class), orders, reservations, listings, payments, mock(NotificationClient.class));
    Order order;
    @BeforeEach void setup() {
        login("alice", "SHOPPER");
        order = Order.builder().id("order1").shopperId("alice").sellerUserId("seller")
            .status(OrderStatus.PENDING_PAYMENT).paymentStatus(PaymentStatus.PAYMENT_PROCESSING)
            .requiresPayment(true).stripePaymentIntentId("pi_order").grossAmountCents(1000L).currency("cad").build();
        when(orders.findById("order1")).thenReturn(Optional.of(order));
    }
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    void login(String id, String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(id, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))));
    }
    @Test void anotherUserCannotReadOrderOrHistory() {
        login("mallory", "SHOPPER");
        assertThrows(ResponseStatusException.class, () -> service.getOrderById("order1"));
        assertThrows(ResponseStatusException.class, () -> service.getOrdersByShopper("alice"));
        assertThrows(ResponseStatusException.class, () -> service.getOrderHistory("alice"));
    }
    @Test void forgedActorCannotCancelOrder() {
        login("mallory", "SHOPPER");
        assertThrows(ResponseStatusException.class, () -> service.cancelOrder("order1", new CancelOrderRequest("alice", "forged")));
        verifyNoInteractions(payments);
        verify(orders, never()).save(any());
    }
    @Test void knowingIntentIdDoesNotProvePayment() {
        when(payments.verifyPayment(order)).thenReturn(false);
        assertThrows(IllegalStateException.class, () -> service.confirmPaymentForShopper("order1", "alice", new PaymentSucceededRequest("pi_order")));
        verifyNoInteractions(listings);
        verify(orders, never()).save(any());
    }
    @Test void mismatchedIntentDoesNotReachPaymentProvider() {
        assertThrows(RuntimeException.class, () -> service.confirmPaymentForShopper("order1", "alice", new PaymentSucceededRequest("pi_other")));
        verifyNoInteractions(payments);
    }
    @Test void cancelledOrderCannotBeResurrectedByPaymentConfirmation() {
        order.setStatus(OrderStatus.CANCELLED);
        assertThrows(IllegalStateException.class, () -> service.confirmPaymentForShopper("order1", "alice", new PaymentSucceededRequest("pi_order")));
        verifyNoInteractions(listings);
    }
    @Test void shopperCannotClaimDonationOrUseAdminDelete() {
        assertThrows(ResponseStatusException.class, () -> service.confirmDonationForShopper("order1", "alice"));
        assertThrows(ResponseStatusException.class, () -> service.deleteOrder("order1"));
        assertThrows(ResponseStatusException.class, () -> service.getAllOrders());
    }
    @Test void genericOrderResponsesHideSecrets() throws Exception {
        order.setPickupCodePlain("123456"); order.setPickupCodeHash("hash"); order.setStripeClientSecret("secret");
        var mapper = new com.fasterxml.jackson.databind.ObjectMapper().findAndRegisterModules();
        String json = mapper.writeValueAsString(order);
        assertFalse(json.contains("pickupCodePlain")); assertFalse(json.contains("pickupCodeHash")); assertFalse(json.contains("stripeClientSecret"));
    }
}
