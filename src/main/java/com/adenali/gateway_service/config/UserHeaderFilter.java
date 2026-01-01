package com.adenali.gateway_service.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class UserHeaderFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(UserHeaderFilter.class);
    // Your signing key (ensure this matches your Auth-Service)
    @Value( "${jwt.secret}")
    private  String SECRET;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("User Header Filter: token");

            try {
                // Generate a secure SecretKey from your string
                // Keys.hmacShaKeyFor handles the RFC 7518 requirements
                SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

                // Parse using 0.12.x syntax
                Claims claims = Jwts.parser()
                        .verifyWith(key) // verifyWith is the replacement for setSigningKey
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String userId = claims.get("username").toString();
                String authorities  = claims.get("authorities").toString();


                // Mutate request for Fleet-Service
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Id", userId)
                        .header("X-authorities", authorities)
                        .header("X-Gateway-Secret", SECRET)
                        .build();
                log.info("User Header Filter: added headers");
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                // Log vulnerability warnings or invalid tokens
                log.error("Invalid JWT token: {}", e.getMessage());
                return chain.filter(exchange);
            }
        }else{
            log.info("User Header Filter: no token");
            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("X-Gateway-Secret", SECRET)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }
    }
    @Override
    public int getOrder() {
        // Run early in the filter chain
        return -5;
    }
}
