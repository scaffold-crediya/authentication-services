package com.jhompo.r2dbc.role;

import com.jhompo.r2dbc.role.data.RoleData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleDataRepository extends ReactiveCrudRepository<RoleData, UUID>, ReactiveQueryByExampleExecutor<RoleData> {

    @Query("SELECT EXISTS (SELECT 1 FROM roles WHERE name = :name)")
    Mono<Boolean> findByName(String name);

    @Query("SELECT * FROM roles WHERE active = :active")
    Flux<RoleData> findByActive(Boolean active);
}
