package com.locally.orders.order_reservationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@org.springframework.context.annotation.Import(com.locally.security.ServiceSecurityConfiguration.class)
@SpringBootApplication
public class OrderReservationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderReservationServiceApplication.class, args);
	}

}
