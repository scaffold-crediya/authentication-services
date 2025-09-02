package co.com.jhompo.r2dbc.user;

import co.com.jhompo.common.Messages.*;
import co.com.jhompo.model.user.User;
import co.com.jhompo.model.user.gateways.UserRepository;
import co.com.jhompo.r2dbc.entity.UserEntity;
import co.com.jhompo.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
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
                        .doOnSuccess(u -> log.info(SYSTEM.OPERATION_SUCCESS, u))
                        .doOnError(e -> log.error(SYSTEM.OPERATION_ERROR, user, e))
        );
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id)
                .doOnSuccess(u -> log.info(USER.DELETED_SUCCESS, u))
                .doOnError(e -> log.error(USER.DELETE_FAILED, id, e));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(userEntity -> mapper.map(userEntity, User.class))
                .doOnSuccess(u -> log.info(USER.EMAIL_ALREADY_EXISTS, u))
                .doOnError(e -> log.error(SYSTEM.OPERATION_ERROR, email, e));
    }

    @Override
    public Mono<Boolean> existsByIdentityDocument(String identityDocument) {
        return repository.existsByIdentityDocument(identityDocument)
                .doOnSuccess(u -> log.info(USER.DOCUMENT_EXISTS, u))
                .doOnError(e -> log.error(SYSTEM.OPERATION_ERROR, identityDocument, e));
    }

    @Override
    public Flux<User> findByEmailIn(List<String> emails) {
        return repository.findByEmailIn(emails).map(this::toEntity)
                .doOnNext(u -> log.info(USER.EMAIL_ALREADY_EXISTS, u))
                .doOnError(e -> log.error(SYSTEM.OPERATION_ERROR,e));
    }
}
