package com.jhompo.r2dbc.user;

import com.jhompo.model.user.User;
import com.jhompo.model.user.gateways.UserRepository;
import com.jhompo.r2dbc.helper.ReactiveAdapterOperations;
import com.jhompo.r2dbc.user.data.UserData;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserData,
        UUID,
        UserDataRepository
        > implements UserRepository {

    public UserRepositoryAdapter(UserDataRepository repository, ObjectMapper mapper) {
        super(repository, mapper, userData -> mapper.map(userData, User.class));
    }

    // Aquí debes implementar los métodos de la interfaz UserRepository
    @Override
    public Mono<User> findById(UUID uuid) {
        return repository.findById(uuid).map(this::toEntity);
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return null;
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return null;
    }

    @Override
    public Mono<Boolean> existsByIdentityDocument(String email) {
        return null;
    }

}
