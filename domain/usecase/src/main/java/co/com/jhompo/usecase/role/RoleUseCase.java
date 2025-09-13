package co.com.jhompo.usecase.role;

import co.com.jhompo.model.role.Role;
import co.com.jhompo.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static co.com.jhompo.util.Messages.ROLE.*;

@RequiredArgsConstructor
public class RoleUseCase {

    private final RoleRepository roleRepository;

    public Mono<Role> createRole(Role status) {
        // Regla de negocio: No permitir la creación de un estado con un nombre duplicado.
        return roleRepository.findByName(status.getName())
                .flatMap(foundRole ->
                        Mono.<Role>error(new IllegalArgumentException(ROL_NAME_ALREADY_EXISTS))
                )
                .switchIfEmpty(
                        Mono.defer(() -> roleRepository.save(status))
                );
    }

    public Mono<Role> getRoleById(Integer id) {
        return roleRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException(ROL_NOT_FOUND)));
    }

    public Flux<Role> getAllRolees() {
        return roleRepository.findAll();
    }

    public Mono<Role> updateRole(Role status) {
        // Asegura que el estado exista antes de intentar actualizarlo
        return roleRepository.findById(status.getId())
                .switchIfEmpty(Mono.error(new RuntimeException(ROL_NOT_FOUND)))
                .flatMap(existingRole -> roleRepository.save(status));
    }

    public Mono<Void> deleteRole(Integer id) {
        return roleRepository.deleteById(id);
    }
}
