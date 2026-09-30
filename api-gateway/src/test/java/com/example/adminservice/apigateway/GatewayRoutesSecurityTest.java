package com.example.adminservice.apigateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ApiGatewayApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class GatewayRoutesSecurityTest {
    @Autowired RouteDefinitionLocator routes;
    @Test void productionCompatibleRoutePropertiesBindWithoutAnInternalRoute() {
        var definitions = routes.getRouteDefinitions().collectList().block();
        assertNotNull(definitions);
        assertEquals(12, definitions.size());
        assertFalse(definitions.stream().anyMatch(route -> route.getId().contains("internal")));
        assertTrue(definitions.stream().anyMatch(route -> route.getId().equals("stripe-payment")));
    }
}
