package co.com.jhompo.r2dbc.user;

import co.com.jhompo.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

//This file is just an example, you should delete or modify it
public interface UserReactiveRepository
        extends ReactiveCrudRepository<UserEntity, UUID>, ReactiveQueryByExampleExecutor<UserEntity> {

    Mono<Boolean> existsByIdentityDocument(String identityDocument);

    Mono<UserEntity> findByEmail(String email);

    Flux<UserEntity> findByEmailIn(List<String> emails);
}
