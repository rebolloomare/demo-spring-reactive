package omare.com.mx.com.demo.reactive.service;

import omare.com.mx.com.demo.reactive.model.User;
import omare.com.mx.com.demo.reactive.security.UserSecurity;
import omare.com.mx.com.demo.reactive.repository.GenericCrudRepository;
import reactor.core.publisher.Mono;

public interface UserService extends GenericCrudRepository<User, String> {

    Mono<User> registerHash(User user);

    Mono<UserSecurity> findByUser(String user);

}
