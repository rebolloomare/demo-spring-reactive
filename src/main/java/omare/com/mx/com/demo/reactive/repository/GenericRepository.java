package omare.com.mx.com.demo.reactive.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericRepository<T, ID> extends R2dbcRepository<T, ID> {
}
