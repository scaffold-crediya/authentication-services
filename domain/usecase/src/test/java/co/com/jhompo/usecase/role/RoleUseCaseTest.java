package co.com.jhompo.usecase.role;

import co.com.jhompo.model.role.Role;
import co.com.jhompo.model.role.gateways.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleUseCase roleUseCase;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = Role.builder()
                .id(1)
                .name("ADMIN")
                .description("Administrator role")
                .build();
    }

    @Test
    @DisplayName("Debería crear un rol exitosamente cuando no existe uno con el mismo nombre")
    void shouldCreateRoleSuccessfully() {
        // Given
        when(roleRepository.findByName(anyString())).thenReturn(Mono.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(Mono.just(testRole));

        // When & Then
        StepVerifier.create(roleUseCase.createRole(testRole))
                .expectNext(testRole)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería lanzar excepción al intentar crear un rol con nombre duplicado")
    void shouldThrowExceptionWhenCreatingRoleWithDuplicateName() {
        // Given
        when(roleRepository.findByName(anyString())).thenReturn(Mono.just(testRole));

        // When & Then
        StepVerifier.create(roleUseCase.createRole(testRole))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("El Rol 'ADMIN' already exists."))
                .verify();
    }

    @Test
    @DisplayName("Debería obtener un rol por ID exitosamente")
    void shouldGetRoleByIdSuccessfully() {
        // Given
        when(roleRepository.findById(anyInt())).thenReturn(Mono.just(testRole));

        // When & Then
        StepVerifier.create(roleUseCase.getRoleById(1))
                .expectNext(testRole)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando no encuentra el rol por ID")
    void shouldThrowExceptionWhenRoleNotFoundById() {
        // Given
        when(roleRepository.findById(anyInt())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(roleUseCase.getRoleById(1))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Role with id 1 not found."))
                .verify();
    }

    @Test
    @DisplayName("Debería obtener todos los roles exitosamente")
    void shouldGetAllRolesSuccessfully() {
        // Given
        Role role2 = Role.builder()
                .id(2)
                .name("USER")
                .description("User role")
                .build();

        when(roleRepository.findAll()).thenReturn(Flux.just(testRole, role2));

        // When & Then
        StepVerifier.create(roleUseCase.getAllRolees())
                .expectNext(testRole)
                .expectNext(role2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería retornar flux vacío cuando no hay roles")
    void shouldReturnEmptyFluxWhenNoRoles() {
        // Given
        when(roleRepository.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(roleUseCase.getAllRolees())
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería actualizar un rol exitosamente cuando existe")
    void shouldUpdateRoleSuccessfullyWhenExists() {
        // Given
        Role updatedRole = testRole.toBuilder()
                .name("SUPER_ADMIN")
                .description("Super Administrator role")
                .build();

        when(roleRepository.findById(anyInt())).thenReturn(Mono.just(testRole));
        when(roleRepository.save(any(Role.class))).thenReturn(Mono.just(updatedRole));

        // When & Then
        StepVerifier.create(roleUseCase.updateRole(updatedRole))
                .expectNext(updatedRole)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería lanzar excepción al intentar actualizar un rol que no existe")
    void shouldThrowExceptionWhenUpdatingNonExistentRole() {
        // Given
        when(roleRepository.findById(anyInt())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(roleUseCase.updateRole(testRole))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Role with id 1 not found."))
                .verify();
    }

    @Test
    @DisplayName("Debería eliminar un rol exitosamente")
    void shouldDeleteRoleSuccessfully() {
        // Given
        when(roleRepository.deleteById(anyInt())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(roleUseCase.deleteRole(1))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería manejar error al eliminar rol")
    void shouldHandleErrorWhenDeletingRole() {
        // Given
        when(roleRepository.deleteById(anyInt())).thenReturn(Mono.error(new RuntimeException("Delete failed")));

        // When & Then
        StepVerifier.create(roleUseCase.deleteRole(1))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Delete failed"))
                .verify();
    }
}
