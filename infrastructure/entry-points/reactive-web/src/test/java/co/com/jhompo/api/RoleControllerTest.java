package co.com.jhompo.api;

import co.com.jhompo.api.dtos.rol.RoleDTO;
import co.com.jhompo.api.dtos.rol.RoleRequestDTO;
import co.com.jhompo.model.role.Role;
import co.com.jhompo.usecase.role.RoleUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {

    @Mock
    private RoleUseCase roleUseCase;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private RoleController roleController;

    private RoleRequestDTO requestDTO;
    private Role role;
    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        requestDTO = RoleRequestDTO.builder().name("TestRole").build();
        role = Role.builder().id(1).name("TestRole").build();
        roleDTO = RoleDTO.builder().id(1).name("TestRole").build();
    }

    @Test
    @DisplayName("Debería crear un nuevo rol exitosamente")
    void shouldCreateNewRoleSuccessfully() {
        // Given
        when(mapper.map(any(RoleRequestDTO.class), any())).thenReturn(role);
        when(roleUseCase.createRole(any(Role.class))).thenReturn(Mono.just(role));
        when(mapper.map(any(Role.class), any())).thenReturn(roleDTO);

        // When & Then
        StepVerifier.create(roleController.create(requestDTO))
                .expectNextMatches(createdRoleDTO -> createdRoleDTO.getName().equals("TestRole"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería listar todos los roles")
    void shouldGetAllRoles() {
        // Given
        when(roleUseCase.getAllRolees()).thenReturn(Flux.just(role));
        when(mapper.map(any(Role.class), any())).thenReturn(roleDTO);

        // When & Then
        StepVerifier.create(roleController.getAll())
                .expectNextMatches(foundRoleDTO -> foundRoleDTO.getName().equals("TestRole"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería encontrar un rol por ID")
    void shouldFindRoleById() {
        // Given
        when(roleUseCase.getRoleById(anyInt())).thenReturn(Mono.just(role));
        when(mapper.map(any(Role.class), any())).thenReturn(roleDTO);

        // When & Then
        StepVerifier.create(roleController.getById(1))
                .expectNextMatches(foundRoleDTO -> foundRoleDTO.getId() == 1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería retornar un error 404 si el rol no es encontrado por ID")
    void shouldReturnNotFoundIfRoleByIdDoesNotExist() {
        // Given
        when(roleUseCase.getRoleById(anyInt())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(roleController.getById(999))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("Debería actualizar un rol existente")
    void shouldUpdateExistingRole() {
        // Given
        when(mapper.map(any(RoleRequestDTO.class), any())).thenReturn(role);
        when(roleUseCase.updateRole(any(Role.class))).thenReturn(Mono.just(role));
        when(mapper.map(any(Role.class), any())).thenReturn(roleDTO);

        // When & Then
        StepVerifier.create(roleController.update(1, requestDTO))
                .expectNextMatches(updatedRoleDTO -> updatedRoleDTO.getName().equals("TestRole"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería eliminar un rol exitosamente")
    void shouldDeleteRoleSuccessfully() {
        // Given
        when(roleUseCase.deleteRole(anyInt())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(roleController.delete(1))
                .verifyComplete();
    }
}