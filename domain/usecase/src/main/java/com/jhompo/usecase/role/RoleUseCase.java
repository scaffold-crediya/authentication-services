package com.jhompo.usecase.role;

import com.jhompo.model.role.Role;
import com.jhompo.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public class RoleUseCase {

    private final RoleRepository rolRepository;

    public RoleUseCase(RoleRepository repository) {
        this.rolRepository = repository;
    }


    public Mono<Role> findById(UUID id) {
        return rolRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Rol no encontrado con ID: " + id)));
    }

    public Flux<Role> findAll() {
        return rolRepository.findAll();
    }

    public Mono<Role> createRol(Role rol) {
        // Valida que el nombre no sea nulo o vacío
        if (rol.getName() == null || rol.getName().isBlank()) {
            return Mono.error(new IllegalArgumentException("El nombre del rol es obligatorio."));
        }

        return rolRepository.findByName(rol.getName())
                .flatMap(foundRol -> Mono.error(new IllegalArgumentException("El nombre de rol ya existe.")))
                .switchIfEmpty(Mono.defer(() -> rolRepository.save(rol)))
                .cast(Role.class);
    }

    public Mono<Role> updatedRol(Role rol) {
        // Valida que el ID del rol a actualizar no sea nulo
        if (rol.getId() == null) {
            return Mono.error(new IllegalArgumentException("El ID del rol es requerido para la actualización."));
        }

        return rolRepository.findById(rol.getId())
                .switchIfEmpty(Mono.error(new RuntimeException("Rol no encontrado para actualizar.")))
                .flatMap(existingRol -> {
                    existingRol.setName(rol.getName());
                    existingRol.setDescription(rol.getDescription());
                    return rolRepository.save(existingRol);
                });
    }

    public Mono<Void> deleteById(UUID id) {
        return rolRepository.deleteById(id);
    }

}
