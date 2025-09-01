package co.com.jhompo.usecase.role;

import co.com.jhompo.model.role.Role;
import co.com.jhompo.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RoleUseCase {

    private final RoleRepository roleRepository;

    public Mono<Role> createRole(Role status) {
        // Regla de negocio: No permitir la creación de un estado con un nombre duplicado.
        return roleRepository.findByName(status.getName())
                .flatMap(foundRole ->
                        Mono.<Role>error(new IllegalArgumentException("Role with name '" + status.getName() + "' already exists."))
                )
                .switchIfEmpty(
                        Mono.defer(() -> roleRepository.save(status))
                );
    }

    public Mono<Role> getRoleById(Integer id) {
        return roleRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Role with id " + id + " not found.")));
    }

    public Flux<Role> getAllRolees() {
        return roleRepository.findAll();
    }

    public Mono<Role> updateRole(Role status) {
        // Asegura que el estado exista antes de intentar actualizarlo
        return roleRepository.findById(status.getId())
                .switchIfEmpty(Mono.error(new RuntimeException("Role with id " + status.getId() + " not found.")))
                .flatMap(existingRole -> roleRepository.save(status));
    }

    public Mono<Void> deleteRole(Integer id) {
        return roleRepository.deleteById(id);
    }
}
