package com.adenali.gateway_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(GatewayServiceApplication.class);

    // Directly use Docker Compose service names
    private final String authServiceUrl = "http://auth-service:8050";
    private final String fleetServiceUrl = "http://fleet-service:8090";
    private final String deviceServiceUrl = "http://fleet-service:8090"; // same as fleet

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                // Route for fleet-service
                .route("fleetms", r -> r.path("/api/fleetservice/**")
                        .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST"))
                        .uri(fleetServiceUrl))

                // Route for device-service
                .route("device-service", r -> r.path("/api/deviceservice/**")
                        .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST"))
                        .uri(deviceServiceUrl))

                // Route for auth-service
                .route("auth-service", r -> r
                        .path("/api/authservice/**")
                        .filters(f -> f
                                .rewritePath("/(?<segment>.*)", "/api/authservice/${segment}")
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST")
                        )
                        .uri("http://auth-service:8050")
                )


                .build();
    }
}
