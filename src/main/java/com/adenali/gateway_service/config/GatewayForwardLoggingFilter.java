package com.adenali.gateway_service.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayForwardLoggingFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayForwardLoggingFilter.class);

    @Bean
    public GlobalFilter logForwardedUrl() {
        return (exchange, chain) -> chain.filter(exchange)
                .doFinally(signalType -> {
                    Object routeObj = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                    if (routeObj instanceof Route route) {
                        var forwardedUri = route.getUri();
                        // Log using slf4j at INFO level
                        log.info("[Gateway] Request {} forwarded to: {} via route {}",
                                exchange.getRequest().getURI(),
                                forwardedUri,
                                route.getId());
                    }
                });
    }
}
