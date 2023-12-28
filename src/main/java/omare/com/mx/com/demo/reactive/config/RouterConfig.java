package omare.com.mx.com.demo.reactive.config;

import omare.com.mx.com.demo.reactive.handler.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routesUser(UserHandler handler){
        return route(GET("/v1/users"), handler::findAll)
                .andRoute(GET("/v2/users/{id}"), handler::findById)
                .andRoute(POST("/v2/users"), handler::save)
                .andRoute(PUT("/v2/users/{id}"), handler::update)
                .andRoute(DELETE("/v2/users/{id}"), handler::delete);
    }

}
