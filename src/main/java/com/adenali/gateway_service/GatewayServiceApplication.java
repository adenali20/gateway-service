package com.adenali.gateway_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayServiceApplication {
    private static final Logger log = LoggerFactory.getLogger(GatewayServiceApplication.class);
    @Value("${service.url}")
    private String serviceUrl;
	public static void main(String[] args) {
		SpringApplication.run(GatewayServiceApplication.class, args);
	}
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("fleetms", r -> r.path("/api/fleetservice/**")
                        .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST"))
                        .uri(serviceUrl+":8090"))
                .route("device-service", r -> r.path("/api/deviceservice/**")
                        .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST"))
                        .uri(serviceUrl+":8090"))
                .route("auth-service", r -> r.path("/api/authservice/**")
                        .filters(f -> f.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_FIRST")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_FIRST"))
                        .uri(serviceUrl+":8050"))
                .build();
    }

}
