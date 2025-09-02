package co.com.jhompo.model.user.gateways;


import co.com.jhompo.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    Mono<User> save(User domainModel);

    Mono<User> findById(UUID id);

    Flux<User> findAll();

    Mono<Void> deleteById(UUID id);

    Mono<User> findByEmail(String email);

    Mono<Boolean> existsByIdentityDocument(String identityDocument);

    Flux<User> findByEmailIn(List<String> emails);
}
