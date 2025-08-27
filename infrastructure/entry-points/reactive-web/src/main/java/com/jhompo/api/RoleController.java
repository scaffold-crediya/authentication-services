package com.jhompo.api;

import com.jhompo.model.role.Role;
import com.jhompo.model.user.User;
import com.jhompo.usecase.role.RoleUseCase;
import com.jhompo.usecase.user.UserUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleUseCase roleUseCase;

    public RoleController(RoleUseCase roleUseCase) {
        this.roleUseCase = roleUseCase;
    }

    @PostMapping
    public Mono<ResponseEntity<Role>> createRole(@RequestBody Role role) {
        return roleUseCase.createRol(role)
                .map(createdRole -> new ResponseEntity<>(createdRole, HttpStatus.CREATED));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Role>> getRoleById(@PathVariable UUID id) {
        return roleUseCase.findById(id)
                .map(role -> new ResponseEntity<>(role, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Flux<Role> getAllRoles() {
        return roleUseCase.findAll();
    }

}
