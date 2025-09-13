package co.com.jhompo.r2dbc.role;

import co.com.jhompo.model.role.Role;
import co.com.jhompo.r2dbc.entity.RoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryAdapterTest {

    @Mock
    private RoleReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    private RoleRepositoryAdapter roleRepositoryAdapter;

    @BeforeEach
    void setUp() {
        roleRepositoryAdapter = new RoleRepositoryAdapter(repository, mapper, transactionalOperator);
    }

    @Test
    void constructor_ShouldInitializeAllDependencies() {
        // Given & When
        RoleRepositoryAdapter adapter = new RoleRepositoryAdapter(repository, mapper, transactionalOperator);

        // Then
        assertNotNull(adapter);
    }

    @Test
    void save_ShouldCallTransactionalOperatorAndReturnRole() {
        // Given
        Role role = Role.builder().id(1).name("ADMIN").build();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(1);
        roleEntity.setName("ADMIN");

        when(mapper.map(role, RoleEntity.class)).thenReturn(roleEntity);
        when(repository.save(roleEntity)).thenReturn(Mono.just(roleEntity));
        when(mapper.map(roleEntity, Role.class)).thenReturn(role);
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Mono<Role> result = roleRepositoryAdapter.save(role);

        // Then
        StepVerifier.create(result)
                .expectNext(role)
                .verifyComplete();

        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void findById_ShouldReturnRole() {
        // Given
        Integer id = 1;
        Role role = Role.builder().id(id).name("ADMIN").build();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(id);
        roleEntity.setName("ADMIN");

        when(repository.findById(id)).thenReturn(Mono.just(roleEntity));
        when(mapper.map(roleEntity, Role.class)).thenReturn(role);

        // When
        Mono<Role> result = roleRepositoryAdapter.findById(id);

        // Then
        StepVerifier.create(result)
                .expectNext(role)
                .verifyComplete();

        verify(repository).findById(id);
    }

    @Test
    void findById_WhenNotFound_ShouldReturnEmpty() {
        // Given
        Integer id = 999;
        when(repository.findById(id)).thenReturn(Mono.empty());

        // When
        Mono<Role> result = roleRepositoryAdapter.findById(id);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(repository).findById(id);
    }

    @Test
    void findAll_ShouldReturnAllRoles() {
        // Given
        Role role1 = Role.builder().id(1).name("ADMIN").build();
        Role role2 = Role.builder().id(2).name("USER").build();

        RoleEntity entity1 = new RoleEntity();
        entity1.setId(1);
        entity1.setName("ADMIN");

        RoleEntity entity2 = new RoleEntity();
        entity2.setId(2);
        entity2.setName("USER");

        when(repository.findAll()).thenReturn(Flux.just(entity1, entity2));
        when(mapper.map(entity1, Role.class)).thenReturn(role1);
        when(mapper.map(entity2, Role.class)).thenReturn(role2);

        // When
        Flux<Role> result = roleRepositoryAdapter.findAll();

        // Then
        StepVerifier.create(result)
                .expectNext(role1, role2)
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void findAll_WhenEmpty_ShouldReturnEmptyFlux() {
        // Given
        when(repository.findAll()).thenReturn(Flux.empty());

        // When
        Flux<Role> result = roleRepositoryAdapter.findAll();

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(repository).findAll();
    }

    @Test
    void deleteById_ShouldCallTransactionalOperator() {
        // Given
        Integer id = 1;
        when(repository.deleteById(id)).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Mono<Void> result = roleRepositoryAdapter.deleteById(id);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(repository).deleteById(id);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void findByName_ShouldReturnRoleWhenFound() {
        // Given
        String name = "ADMIN";
        Role role = Role.builder().id(1).name(name).build();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setId(1);
        roleEntity.setName(name);

        when(repository.findByName(name)).thenReturn(Mono.just(roleEntity));
        when(mapper.map(roleEntity, Role.class)).thenReturn(role);

        // When
        Mono<Role> result = roleRepositoryAdapter.findByName(name);

        // Then
        StepVerifier.create(result)
                .expectNext(role)
                .verifyComplete();

        verify(repository).findByName(name);
        verify(mapper).map(roleEntity, Role.class);
    }

    @Test
    void findByName_WhenNotFound_ShouldReturnEmpty() {
        // Given
        String name = "NONEXISTENT";
        when(repository.findByName(name)).thenReturn(Mono.empty());

        // When
        Mono<Role> result = roleRepositoryAdapter.findByName(name);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(repository).findByName(name);
        verifyNoInteractions(mapper);
    }

    @Test
    void findByName_WithNullName_ShouldHandleGracefully() {
        // Given
        String name = null;
        when(repository.findByName(name)).thenReturn(Mono.empty());

        // When
        Mono<Role> result = roleRepositoryAdapter.findByName(name);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(repository).findByName(name);
    }

    @Test
    void findByName_WithEmptyName_ShouldHandleGracefully() {
        // Given
        String name = "";
        when(repository.findByName(name)).thenReturn(Mono.empty());

        // When
        Mono<Role> result = roleRepositoryAdapter.findByName(name);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(repository).findByName(name);
    }
}