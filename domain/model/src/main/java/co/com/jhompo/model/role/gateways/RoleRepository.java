package co.com.jhompo.model.role.gateways;

import co.com.jhompo.model.role.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface RoleRepository {
    Mono<Role> save(Role status);
    Mono<Role> findById(Integer id);
    Flux<Role> findAll();
    Mono<Void> deleteById(Integer id);
    Mono<Role> findByName(String name);
}
