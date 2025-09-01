package com.jhompo.r2dbc;

import com.jhompo.model.role.Role;
import com.jhompo.model.role.gateways.RoleRepository;
import org.reactivecommons.utils.ObjectMapper;
import com.jhompo.r2dbc.helper.ReactiveAdapterOperations;
import com.jhompo.r2dbc.entity.RoleEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class RoleRepositoryAdapter
        extends ReactiveAdapterOperations<Role, RoleEntity, UUID, RoleReactiveRepository>
        implements RoleRepository {

    public RoleRepositoryAdapter(RoleReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, roleEntity -> mapper.map(roleEntity, Role.class));
    }


    @Override
    public Flux<Role> findAll() {
        return repository.findAll()
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