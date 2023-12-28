package omare.com.mx.com.demo.reactive.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        ServerHttpResponse reactiveResponse = exchange.getResponse();
        reactiveResponse.setStatusCode(HttpStatus.FORBIDDEN);
        return new AuthFailureHandler().formatResponse(reactiveResponse);
    }
}
