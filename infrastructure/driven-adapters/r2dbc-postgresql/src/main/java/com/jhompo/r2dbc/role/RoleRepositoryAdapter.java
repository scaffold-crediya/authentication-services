package com.jhompo.r2dbc.role;

import com.jhompo.model.role.Role;
import com.jhompo.model.role.gateways.RoleRepository;
import org.reactivecommons.utils.ObjectMapper;
import com.jhompo.r2dbc.helper.ReactiveAdapterOperations;
import com.jhompo.r2dbc.role.data.RoleData;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class RoleRepositoryAdapter
        extends ReactiveAdapterOperations<Role, RoleData, UUID, RoleDataRepository >
        implements RoleRepository {

    public RoleRepositoryAdapter(RoleDataRepository repository, ObjectMapper mapper) {
        super(repository, mapper, roleData -> mapper.map(roleData, Role.class));
    }


    @Override
    public Mono<Boolean> findByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public Flux<Role> findAll() {
        return repository.findByActive(true)
                .map(this::toEntity);
    }

    @Override
    public Mono<Role> findById(UUID uuid) {
        return repository.findById(uuid)
                .map(this::toEntity);
    }

    @Override
    public Mono<Void> deleteById(UUID uuid) {
        return repository.deleteById(uuid);
    }
}