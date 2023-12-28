package omare.com.mx.com.demo.reactive.controller;

import omare.com.mx.com.demo.reactive.model.User;
import omare.com.mx.com.demo.reactive.repository.UserRepository;
import omare.com.mx.com.demo.reactive.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = UserController.class)
@Import(UserServiceImpl.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private WebProperties.Resources resources;

    @Test
    void findAll() {
        User user1 = new User("1", "Omare");
        User user2 = new User("2", "rebollo");
        User user3 = new User("3", "rebolloomare");

        List<User> userList = Arrays.asList(user1, user2, user3);

        when(userRepository.findAll()).thenReturn(Flux.fromIterable(userList));
        webTestClient.get()
                .uri("/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(User.class)
                .hasSize(3);
    }

    @Test
    void findById() {
        User user = new User("1", "omare");

        when(userRepository.findById("1")).thenReturn(Mono.just(user));

        webTestClient.get()
                .uri("/users/" + user.getUuid())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(User.class);
    }

    @Test
    void save() {
        User user = new User("1", "simio");
        when(userRepository.save(any())).thenReturn(Mono.just(user));

        webTestClient.post()
                .uri("/users")
                .body(Mono.just(user), User.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.uuid").isNotEmpty()
                .jsonPath("$.name").isNotEmpty();
    }

    @Test
    void update() {
        User user = new User("1", "rebolloomare");

        when(userRepository.findById("1")).thenReturn(Mono.just(user));
        when(userRepository.save(any())).thenReturn(Mono.just(user));

        webTestClient.put()
                .uri("/users/" + user.getUuid())
                .body(Mono.just(user), User.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.uuid").isNotEmpty();
    }

    @Test
    void delete() {
        User user = new User("2", "rebolloomare");

        when(userRepository.findById("2")).thenReturn(Mono.just(user));
        when(userRepository.deleteById("2")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/users/" + user.getUuid())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();


    }
}