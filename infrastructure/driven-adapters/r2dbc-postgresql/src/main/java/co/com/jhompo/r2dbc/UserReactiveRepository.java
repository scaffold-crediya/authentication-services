package co.com.jhompo.r2dbc;

import co.com.jhompo.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

//This file is just an example, you should delete or modify it
public interface UserReactiveRepository
        extends ReactiveCrudRepository<UserEntity, UUID>, ReactiveQueryByExampleExecutor<UserEntity> {

    //Mono<Boolean> deleteById(UUID id);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByIdentityDocument(String identityDocument);
}
