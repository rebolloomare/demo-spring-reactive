package omare.com.mx.com.demo.reactive.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import omare.com.mx.com.demo.reactive.model.User;
import omare.com.mx.com.demo.reactive.pagination.PageSupport;
import omare.com.mx.com.demo.reactive.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
import static reactor.function.TupleUtils.function;

@Tag(name = "user", description = "The User API")
@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/users")
    public Mono<ResponseEntity<Flux<User>>> findAll(){
        Flux<User> fx = userService.findAll();
        return Mono.just(ResponseEntity
                        .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/users/{id}")
    public Mono<ResponseEntity<User>> findById(@PathVariable("id") String id){
        return userService.findById(id)
                .map(u -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(u))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/users")
    public Mono<ResponseEntity<User>> save(@Valid @RequestBody User user, final ServerHttpRequest request){
        return userService.save(user)
                .map(u -> ResponseEntity
                        .created(URI
                                .create(request
                                        .getURI()
                                        .toString()
                                        .concat("/")
                                        .concat(u.getUuid())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(u)
                );
    }

    @PutMapping("/users/{id}")
    public Mono<ResponseEntity<User>> update(@PathVariable("id") String id, @RequestBody User user){

        Mono<User> monoBody = Mono.just(user);
        Mono<User> monoDB = userService.findById(id);

        return monoDB.zipWith(monoBody, (uFromDB, uToUpdate) -> {
            uFromDB.setUuid(id);
            uFromDB.setUserName(uToUpdate.getUserName());
            return  uFromDB;
        })
                .flatMap(userService::update)
                .map(u -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(u))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/users/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id){
        return userService.findById(id)
                .flatMap(u -> userService.deleteById(id)
                    .thenReturn(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))
                ).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/usersH1/{id}")
    public Mono<EntityModel<User>> hateoasFindById(@PathVariable("id") String id){
        Mono<Link> link = linkTo(methodOn(UserController.class).findById(id)).withSelfRel().toMono();

        return userService.findById(id)
                .zipWith(link, EntityModel::of);
    }

    @GetMapping("/usersH2/{id}")
    public Mono<EntityModel<User>> hateoasFindById2Links(@PathVariable("id") String id){
        Mono<Link> link1 = linkTo(methodOn(UserController.class).findById(id)).withSelfRel().toMono();
        Mono<Link> link2 = linkTo(methodOn(UserController.class).findById(id)).withSelfRel().toMono();

        return link1
                .zipWith(link2)
                .map(function((lk1, lk2) -> Links.of(lk1, lk2)))
                .zipWith(userService.findById(id), (lk3, p) -> EntityModel.of(p, lk3));
    }

    @GetMapping("/users/pageable")
    public Mono<ResponseEntity<PageSupport<User>>> getPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size
    ){
        Pageable pageRequest = PageRequest.of(page, size);
        return userService.getPage(pageRequest)
                .map(p -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p)
                )
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

}