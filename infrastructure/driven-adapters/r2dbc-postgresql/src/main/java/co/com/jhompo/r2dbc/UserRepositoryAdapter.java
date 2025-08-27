package co.com.jhompo.r2dbc;

import co.com.jhompo.model.user.User;
import co.com.jhompo.model.user.gateways.UserRepository;
import co.com.jhompo.r2dbc.entity.UserEntity;
import co.com.jhompo.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class UserRepositoryAdapter
        extends ReactiveAdapterOperations<User,UserEntity,UUID, UserReactiveRepository>
        implements UserRepository {

    private final Logger log = LoggerFactory.getLogger(UserRepositoryAdapter.class);
    private final TransactionalOperator transactionalOperator;

    public UserRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
           super(repository, mapper, d -> mapper.map(d, User.class));
           this.transactionalOperator = transactionalOperator;
    }


    @Override
    public Mono<User> save(User user) {
        return transactionalOperator.transactional(
                super.save(user)
                        .doOnSuccess(u -> log.info("Guardado en BD: {}", u))
                        .doOnError(e -> log.error("Error al guardar el usuario: {}", user, e))
        );
    }

    @Override
    public Mono<Boolean> deleteById(UUID id) {
        return null; //repository.findById(id);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.existsByEmail(email)
                .doOnSuccess(u -> log.info("Guardado en BD: {}", u))
                .doOnError(e -> log.error("Error al guardar el usuario: {}", email, e));
    }

    @Override
    public Mono<Boolean> existsByIdentityDocument(String identityDocument) {
        return repository.existsByIdentityDocument(identityDocument)
                .doOnSuccess(u -> log.info("Guardado en BD: {}", u))
                .doOnError(e -> log.error("Error al guardar el usuario: {}", identityDocument, e));
    }
}
