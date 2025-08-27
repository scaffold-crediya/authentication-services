package co.com.jhompo.model.user.gateways;


import co.com.jhompo.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository {
    Mono<User> save(User domainModel);

    Mono<User> findById(UUID id);

    Flux<User> findAll();

    Mono<Boolean> deleteById(UUID id);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByIdentityDocument(String identityDocument);
}
