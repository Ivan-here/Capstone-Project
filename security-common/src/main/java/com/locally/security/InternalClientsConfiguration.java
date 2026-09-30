package com.locally.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.Set;

/** Never attach service credentials to arbitrary external URLs. */
@Configuration(proxyBeanMethods = false)
public class InternalClientsConfiguration {
    public static boolean trusted(URI uri) {
        return uri.getUserInfo() == null && Set.of("http", "https").contains(uri.getScheme())
            && uri.getPort() >= 8081 && uri.getPort() <= 8091
            && Set.of("localhost", "127.0.0.1", "identity-service", "profile-service", "listing-service",
                "verification-service", "order-reservation-service", "notifications-service", "admin-service",
                "community-service", "review-service", "stripe-payment-service", "follow-service").contains(uri.getHost());
    }
    @Bean
    static BeanPostProcessor internalRestTemplates(@Value("${INTERNAL_SHARED_SECRET}") String secret) {
        return new BeanPostProcessor() {
            public Object postProcessAfterInitialization(Object bean, String name) {
                if (bean instanceof RestTemplate rest) rest.getInterceptors().add((req, body, execution) -> {
                    if (trusted(req.getURI())) req.getHeaders().set("X-Internal-Secret", secret);
                    return execution.execute(req, body);
                });
                return bean;
            }
        };
    }
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "feign.RequestInterceptor")
    static class FeignClients {
        @Bean
        feign.RequestInterceptor internalServiceCredentials(@Value("${INTERNAL_SHARED_SECRET}") String secret) {
            return template -> {
                if (template.feignTarget() != null && trusted(URI.create(template.feignTarget().url()))) {
                    template.removeHeader("X-Internal-Secret");
                    template.header("X-Internal-Secret", secret);
                }
            };
        }
    }
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "org.springframework.web.reactive.function.client.WebClient")
    static class ReactiveClients {
        @Bean
        static BeanPostProcessor internalWebClients(@Value("${INTERNAL_SHARED_SECRET}") String secret) {
            return new BeanPostProcessor() {
                public Object postProcessAfterInitialization(Object bean, String name) {
                    if (bean instanceof org.springframework.web.reactive.function.client.WebClient client) {
                        return client.mutate().filter((req, next) -> {
                            if (!trusted(req.url())) return next.exchange(req);
                            return next.exchange(org.springframework.web.reactive.function.client.ClientRequest.from(req)
                                .headers(h -> h.set("X-Internal-Secret", secret)).build());
                        }).build();
                    }
                    return bean;
                }
            };
        }
    }
}
