package omare.com.mx.com.demo.reactive.service;

import lombok.AllArgsConstructor;
import omare.com.mx.com.demo.reactive.model.User;
import omare.com.mx.com.demo.reactive.repository.GenericCrudRepositoryImpl;
import omare.com.mx.com.demo.reactive.repository.GenericRepository;
import omare.com.mx.com.demo.reactive.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl extends GenericCrudRepositoryImpl<User, String> implements UserService {

    private UserRepository userRepository;

    @Override
    protected GenericRepository<User, String> getRepository() {
        return userRepository;
    }
}
