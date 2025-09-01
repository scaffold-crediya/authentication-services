package co.com.jhompo.r2dbc.role;

import co.com.jhompo.model.role.Role;
import co.com.jhompo.model.role.gateways.RoleRepository;
import co.com.jhompo.model.user.User;
import co.com.jhompo.r2dbc.entity.RoleEntity;
import co.com.jhompo.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class RoleRepositoryAdapter extends ReactiveAdapterOperations<Role, RoleEntity, Integer, RoleReactiveRepository>
        implements RoleRepository {

    private static final Logger log = LoggerFactory.getLogger(RoleRepositoryAdapter.class);
    private final TransactionalOperator transactionalOperator;


    public RoleRepositoryAdapter(RoleReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, entity -> mapper.map(entity, Role.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<Role> save(Role status) {
        log.info("Iniciando guardado en BD para el estado: {}", status.getName());
        return transactionalOperator.transactional(
                super.save(status)
                        .doOnSuccess(s -> log.info("Guardado exitoso en BD: {}", s.getName()))
                        .doOnError(e -> log.error("Error al guardar el estado: {}", status.getName(), e))
        );
    }

    @Override
    public Mono<Role> findById(Integer id) {
        return super.findById(id);
    }

    @Override
    public Flux<Role> findAll() {
        return super.findAll();
    }

    @Override
    public Mono<Void> deleteById(Integer id) {
        log.info("Iniciando eliminación en BD para el estado ID: {}", id);
        return transactionalOperator.transactional(
                repository.deleteById(id)
                        .doOnSuccess(v -> log.info("Eliminación exitosa en BD: {}", id))
                        .doOnError(e -> log.error("Error al eliminar el estado: {}", id, e))
        );
    }

    @Override
    public Mono<Role> findByName(String name) {
        return repository.findByName(name)
                .map(rolEntity -> mapper.map(rolEntity, Role.class));
    }
}


