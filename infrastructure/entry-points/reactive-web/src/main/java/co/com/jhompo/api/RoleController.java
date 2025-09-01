package co.com.jhompo.api;

import co.com.jhompo.api.dtos.rol.RoleDTO;
import co.com.jhompo.api.dtos.rol.RoleRequestDTO;
import co.com.jhompo.model.role.Role;
import co.com.jhompo.usecase.role.RoleUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Gestión de roles del sistema")
public class RoleController {

    private final RoleUseCase roleUseCase;
    private final ModelMapper mapper;


    @Operation(summary = "Crear nuevo Rol")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RoleDTO> create(@RequestBody RoleRequestDTO requestDTO) {
        var role = mapper.map(requestDTO, Role.class);  // El ID '0' es un placeholder, ya que la base de datos lo generará.
        return roleUseCase.createRole(role).map(obj->mapper.map(obj, RoleDTO.class));
    }

    @Operation(summary = "Listar todos los roles")
    @GetMapping
    public Flux<RoleDTO> getAll() {
        return roleUseCase.getAllRolees()
                .map(role-> mapper.map(role, RoleDTO.class));
    }

    @Operation(summary = "Buscar rol por ID")
    @GetMapping("/{id}")
    public Mono<RoleDTO> getById(@PathVariable Integer id) {
        return roleUseCase.getRoleById(id)
                .map(role->mapper.map(role, RoleDTO.class))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Role with id " + id + " not found.")));

    }

    @Operation(summary = "Actualizar rol existente")
    @PutMapping("/{id}")
    public Mono<RoleDTO> update(@PathVariable Integer id, @RequestBody RoleRequestDTO requestDTO) {

        return roleUseCase.updateRole( mapper.map(requestDTO, Role.class))
                .map(role ->mapper.map(role, RoleDTO.class));
    }

    @Operation(summary = "Eliminar rol")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Integer id) {
        return roleUseCase.deleteRole(id);
    }
}
