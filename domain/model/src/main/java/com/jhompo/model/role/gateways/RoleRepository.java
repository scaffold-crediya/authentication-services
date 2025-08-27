package com.jhompo.model.role.gateways;

import com.jhompo.model.gateways.GenericRepository;
import com.jhompo.model.role.Role;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleRepository extends GenericRepository<Role, UUID> {
    Mono<Boolean> findByName(String name);
}
