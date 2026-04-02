package com.locally.orders.order_reservationservice.config;

import com.locally.orders.order_reservationservice.model.OrderItem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(new LegacyOrderItemReadConverter()));
    }

    @ReadingConverter
    static class LegacyOrderItemReadConverter implements Converter<String, OrderItem> {
        @Override
        public OrderItem convert(String source) {
            if (source == null || source.isBlank()) {
                return null;
            }

            // Older orders stored plain string items. Preserve the identifier/title
            // so legacy records can still load in admin and history screens.
            return OrderItem.builder()
                    .listingId(source)
                    .title(source)
                    .quantity(1)
                    .unitPriceCents(0L)
                    .lineTotalCents(0L)
                    .build();
        }
    }
}
