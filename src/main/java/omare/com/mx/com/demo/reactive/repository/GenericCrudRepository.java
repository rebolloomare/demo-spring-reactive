package omare.com.mx.com.demo.reactive.repository;

import omare.com.mx.com.demo.reactive.pagination.PageSupport;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericCrudRepository<T, ID>{

    Mono<T> save(T t);

    Mono<T> update(T t);

    Flux<T> findAll();

    Mono<T> findById(ID id);

    Mono<Void> deleteById(ID id);

    Mono<PageSupport<T>> getPage(Pageable page);

}
