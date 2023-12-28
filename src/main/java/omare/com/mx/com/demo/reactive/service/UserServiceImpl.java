package omare.com.mx.com.demo.reactive.service;

import lombok.AllArgsConstructor;
import omare.com.mx.com.demo.reactive.model.User;
import omare.com.mx.com.demo.reactive.security.UserSecurity;
import omare.com.mx.com.demo.reactive.repository.GenericCrudRepositoryImpl;
import omare.com.mx.com.demo.reactive.repository.GenericRepository;
import omare.com.mx.com.demo.reactive.repository.RoleRepository;
import omare.com.mx.com.demo.reactive.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl extends GenericCrudRepositoryImpl<User, String> implements UserService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected GenericRepository<User, String> getRepository() {
        return userRepository;
    }

    @Override
    public Mono<User> registerHash(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public Mono<UserSecurity> findByUser(String user) {
        Mono<User> monoUser = userRepository.findOneByUsername(user);

        List<String> roles = new ArrayList<>();

        return monoUser.flatMap(u -> {
            return Flux.fromIterable(u.getRoles())
                    .flatMap(rol -> {
                        return roleRepository.findById(rol.getId())
                                .map(r -> {
                                    roles.add(r.getName());
                                    return r;
                                });
                    }).collectList().flatMap(list -> {
                        u.setRoles(list);
                        return Mono.just(u);
                    });
        })
        .flatMap(u -> {
            UserSecurity usr = new UserSecurity(u.getUsername(), u.getPassword(), u.getStatus(), roles);
            return Mono.just(usr);
        });
    }
}
