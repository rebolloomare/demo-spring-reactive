package omare.com.mx.com.demo.reactive.repository;

import omare.com.mx.com.demo.reactive.model.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends GenericRepository<User, String> {

    Mono<User> findOneByUsername(String user);

}
