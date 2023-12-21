package omare.com.mx.com.demo.reactive.service;

import omare.com.mx.com.demo.reactive.model.User;
import omare.com.mx.com.demo.reactive.repository.GenericCrudRepository;

public interface UserService extends GenericCrudRepository<User, String> {
}
