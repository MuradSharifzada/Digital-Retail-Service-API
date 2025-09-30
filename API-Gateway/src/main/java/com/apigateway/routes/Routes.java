package com.apigateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;
import java.util.Map;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;

@Configuration
public class Routes {

    // Externalized service URLs (set in application.yml or properties)
    @Value("${services.product}")
    private String productServiceUrl;

    @Value("${services.order}")
    private String orderServiceUrl;

    @Value("${services.inventory}")
    private String inventoryServiceUrl;

    // Product service route
    @Bean
    public RouterFunction<ServerResponse> productServerRoute() {
        return route("product_service")
                .route(RequestPredicates.path("/api/product"),
                        HandlerFunctions.http(productServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "productServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
        return route("product_service_swagger")
                .route(RequestPredicates.path("/aggregate/product-service/v3/api-docs"),
                        HandlerFunctions.http(productServiceUrl))
                .filter(setPath("/v3/api-docs"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "productServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    // Order service route
    @Bean
    public RouterFunction<ServerResponse> orderServerRoute() {
        return route("order_service")
                .route(RequestPredicates.path("/api/order"),
                        HandlerFunctions.http(orderServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "orderServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
        return route("order_service_swagger")
                .route(RequestPredicates.path("/aggregate/order-service/v3/api-docs"),
                        HandlerFunctions.http(orderServiceUrl))
                .filter(setPath("/v3/api-docs"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "orderServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    // Inventory service route
    @Bean
    public RouterFunction<ServerResponse> inventoryServerRoute() {
        return route("inventory_service")
                .route(RequestPredicates.path("/api/inventory"),
                        HandlerFunctions.http(inventoryServiceUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "inventoryServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
        return route("inventory_service_swagger")
                .route(RequestPredicates.path("/aggregate/inventory-service/v3/api-docs"),
                        HandlerFunctions.http(inventoryServiceUrl))
                .filter(setPath("/v3/api-docs"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker(
                        "inventoryServiceCircuitBreaker",
                        URI.create("forward:/fallbackRoute")))
                .build();
    }

    // Fallback route (supports ALL HTTP methods, returns JSON)
    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route("fallbackServerRoute")
                .route(RequestPredicates.all(),
                        request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body(Map.of("message", "Service not available")))
                .build();
    }
}
