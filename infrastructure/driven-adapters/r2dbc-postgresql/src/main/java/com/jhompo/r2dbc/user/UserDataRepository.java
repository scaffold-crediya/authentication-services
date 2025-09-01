package com.jhompo.r2dbc.user;

import com.jhompo.r2dbc.user.data.UserData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface UserDataRepository extends ReactiveCrudRepository<UserData, UUID>, ReactiveQueryByExampleExecutor<UserData> {

    Mono<UserData> findByUsername(String username);
}