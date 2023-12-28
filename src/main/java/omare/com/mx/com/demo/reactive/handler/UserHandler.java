package omare.com.mx.com.demo.reactive.handler;

import lombok.AllArgsConstructor;
import omare.com.mx.com.demo.reactive.model.User;
import omare.com.mx.com.demo.reactive.service.UserService;
import omare.com.mx.com.demo.reactive.validators.RequestValidator;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
@AllArgsConstructor
public class UserHandler {

    private UserService userService;

    private RequestValidator requestValidator;

    public Mono<ServerResponse> findAll(ServerRequest request){
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userService.findAll(), User.class)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findById(ServerRequest request){
        String id = request.pathVariable("id");
        return userService.findById(id)
                .flatMap(u -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(u))
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> save(ServerRequest request){
        Mono<User> user = request.bodyToMono(User.class);
        return user
                .flatMap(requestValidator::validate)
                .flatMap(userService::save)
                .flatMap(u -> ServerResponse.created(URI
                        .create(request
                                .uri()
                                .toString()
                                .concat(u.getUuid())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(u))
                );
    }

    public Mono<ServerResponse> update(ServerRequest request){
        String id = request.pathVariable("id");

        Mono<User> userRequest = request.bodyToMono(User.class);
        Mono<User> userDB = userService.findById(id);

        return userDB.zipWith(userRequest, (uFromDB, uToUpdate) -> {
            uFromDB.setUuid(id);
            uFromDB.setName(uToUpdate.getName());
            return  uFromDB;
        })
                .flatMap(requestValidator::validate)
                .flatMap(userService::update)
                .flatMap(u -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(u)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request){
        String id = request.pathVariable("id");
        return userService.findById(id)
                .flatMap(u -> userService.deleteById(u.getUuid())
                        .then(ServerResponse.noContent().build())
                )
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}
