package com.jhompo.model.role.gateways;

import com.jhompo.model.gateways.GenericRepository;
import com.jhompo.model.role.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleRepository {
    Mono<Role> save(Role domainModel);

    Mono<Role> findById(UUID id);

    Flux<Role> findAll();

    Mono<Void> deleteById(UUID id);

    Mono<Boolean> findByName(String name);
}
