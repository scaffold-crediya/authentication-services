package com.jhompo.model.user.gateways;

import com.jhompo.model.gateways.GenericRepository;
import com.jhompo.model.user.User;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends GenericRepository<User, UUID> {

    Mono<Boolean> existsByEmail(String email);
    Mono<Boolean> existsByIdentityDocument(String email);
}
