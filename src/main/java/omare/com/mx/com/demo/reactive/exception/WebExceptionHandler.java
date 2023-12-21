package omare.com.mx.com.demo.reactive.exception;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Order(-1)
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {


    /**
     * Create a new {@code AbstractErrorWebExceptionHandler}.
     *
     * @param errorAttributes    the error attributes
     * @param resources          the resources configuration properties
     * @param applicationContext the application context
     * @since 2.4.0
     */
    public WebExceptionHandler(ErrorAttributes errorAttributes,
                               WebProperties.Resources resources,
                               ApplicationContext applicationContext,
                               ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> generalError = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        Map<String, Object> customError = new HashMap<>();

        var status= HttpStatus.INTERNAL_SERVER_ERROR;
        String statusCode = String.valueOf(generalError.get("status"));

        switch (statusCode){
            case "400":
                customError.put("message", "Bad Request");
                customError.put("status", "400");
                status = HttpStatus.BAD_REQUEST;
                break;
            case "404":
                status = HttpStatus.NOT_FOUND;
                break;
            case "500":
                customError.put("message", "Internal Server Error");
                customError.put("status", "500");
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
        }

        return ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(customError));
    }


}
