package omare.com.mx.com.demo.reactive.repository;

import omare.com.mx.com.demo.reactive.pagination.PageSupport;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public abstract class GenericCrudRepositoryImpl<T, ID> implements GenericCrudRepository<T, ID> {

    protected abstract GenericRepository<T, ID> getRepository();

    @Override
    public Mono<T> save(T t) {
        return getRepository().save(t);
    }

    @Override
    public Mono<T> update(T t) {
        return getRepository().save(t);
    }

    @Override
    public Flux<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    public Mono<T> findById(ID id) {
        return getRepository().findById(id);
    }

    @Override
    public Mono<Void> deleteById(ID id) {
        return getRepository().deleteById(id);
    }

    @Override
    public Mono<PageSupport<T>> getPage(Pageable page){
        return getRepository().findAll()
                .collectList()
                .map(list -> new PageSupport<>(
                        list.stream()
                                .skip((long) page.getPageNumber() * page.getPageSize())
                                .limit(page.getPageSize())
                                .collect(Collectors.toList()),
                        page.getPageNumber(),
                        page.getPageSize(),
                        list.size())
                );
    }

}
