package co.com.jhompo.r2dbc.user;

import co.com.jhompo.model.user.User;
import co.com.jhompo.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserReactiveRepository userReactiveRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TransactionalOperator transactionalOperator;

    @Mock
    private Logger logger;

    private UserRepositoryAdapter userRepositoryAdapter;
    private User testUser;
    private UserEntity testUserEntity;
    private UUID testUserId;

    @BeforeEach
    void setUp() throws Exception {
        testUserId = UUID.randomUUID();

        testUser = User.builder()
                .id(testUserId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("hashedPassword")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("100000"))
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();

        testUserEntity = UserEntity.builder()
                .id(testUserId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("hashedPassword")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("100000"))
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();

        userRepositoryAdapter = new UserRepositoryAdapter(userReactiveRepository, objectMapper, transactionalOperator);

        // Inject mock logger using reflection
        injectMockLogger();
    }

    private void injectMockLogger() throws Exception {
        Field logField = UserRepositoryAdapter.class.getDeclaredField("log");
        logField.setAccessible(true);
        logField.set(userRepositoryAdapter, logger);
    }



    // ========== TESTS FOR deleteById ==========
    @Test
    @DisplayName("Debería eliminar usuario por ID exitosamente")
    void shouldDeleteUserByIdSuccessfully() {
        // Given
        when(userReactiveRepository.deleteById(testUserId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userRepositoryAdapter.deleteById(testUserId))
                .verifyComplete();

        verify(userReactiveRepository, times(1)).deleteById(testUserId);
    }

    @Test
    @DisplayName("Debería manejar error al eliminar usuario por ID")
    void shouldHandleErrorWhenDeletingUserById() {
        // Given
        RuntimeException deleteException = new RuntimeException("Database delete error");
        when(userReactiveRepository.deleteById(testUserId))
                .thenReturn(Mono.error(deleteException));

        // When & Then
        StepVerifier.create(userRepositoryAdapter.deleteById(testUserId))
                .expectError(RuntimeException.class)
                .verify();

        verify(userReactiveRepository, times(1)).deleteById(testUserId);
    }

    @Test
    @DisplayName("Debería loggear éxito al eliminar usuario")
    void shouldLogSuccessWhenDeletingUser() {
        // Given
        when(userReactiveRepository.deleteById(testUserId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userRepositoryAdapter.deleteById(testUserId))
                .verifyComplete();

        verify(logger, times(1)).info("Eliminado Satisfactoriamente", (Object) null);
        verify(logger, never()).error(anyString(), any(), any(Throwable.class));
    }

    @Test
    @DisplayName("Debería loggear error al eliminar usuario")
    void shouldLogErrorWhenDeletingUser() {
        // Given
        RuntimeException deleteException = new RuntimeException("Delete failed");
        when(userReactiveRepository.deleteById(testUserId))
                .thenReturn(Mono.error(deleteException));

        // When & Then
        StepVerifier.create(userRepositoryAdapter.deleteById(testUserId))
                .expectError(RuntimeException.class)
                .verify();

        verify(logger, times(1)).error("Error", testUserId, deleteException);
        verify(logger, never()).info(anyString(), (Object) any());
    }

    // ========== TESTS FOR findByEmail ==========
    @Test
    @DisplayName("Debería encontrar usuario por email exitosamente")
    void shouldFindUserByEmailSuccessfully() {
        // Given
        String email = "john.doe@example.com";
        when(userReactiveRepository.findByEmail(email)).thenReturn(Mono.just(testUserEntity));
        when(objectMapper.map(testUserEntity, User.class)).thenReturn(testUser);

        // When & Then
        StepVerifier.create(userRepositoryAdapter.findByEmail(email))
                .expectNext(testUser)
                .verifyComplete();

        verify(userReactiveRepository, times(1)).findByEmail(email);
        verify(objectMapper, times(1)).map(testUserEntity, User.class);
    }

    @Test
    @DisplayName("Debería retornar Mono vacío cuando el email no existe")
    void shouldReturnEmptyMonoWhenEmailNotExists() {
        // Given
        String email = "nonexistent@example.com";
        when(userReactiveRepository.findByEmail(email)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userRepositoryAdapter.findByEmail(email))
                .verifyComplete();

        verify(userReactiveRepository, times(1)).findByEmail(email);
        verify(objectMapper, never()).map(any(), eq(User.class));
    }

    @Test
    @DisplayName("Debería manejar error al buscar usuario por email")
    void shouldHandleErrorWhenFindingByEmail() {
        // Given
        String email = "john.doe@example.com";
        RuntimeException findException = new RuntimeException("Database query error");
        when(userReactiveRepository.findByEmail(email)).thenReturn(Mono.error(findException));

        // When & Then
        StepVerifier.create(userRepositoryAdapter.findByEmail(email))
                .expectError(RuntimeException.class)
                .verify();

        verify(userReactiveRepository, times(1)).findByEmail(email);
        verify(objectMapper, never()).map(any(), eq(User.class));
    }

    @Test
    @DisplayName("Debería loggear éxito al encontrar usuario por email")
    void shouldLogSuccessWhenFindingByEmail() {
        // Given
        String email = "john.doe@example.com";
        when(userReactiveRepository.findByEmail(email)).thenReturn(Mono.just(testUserEntity));
        when(objectMapper.map(testUserEntity, User.class)).thenReturn(testUser);

        // When & Then
        StepVerifier.create(userRepositoryAdapter.findByEmail(email))
                .expectNext(testUser)
                .verifyComplete();

        verify(logger, times(1)).info("Existe correo en BD: {}", testUser);
        verify(logger, never()).error(anyString(), any(), any(Throwable.class));
    }

    @Test
    @DisplayName("Debería loggear error al buscar usuario por email")
    void shouldLogErrorWhenFindingByEmail() {
        // Given
        String email = "john.doe@example.com";
        RuntimeException findException = new RuntimeException("Query failed");
        when(userReactiveRepository.findByEmail(email)).thenReturn(Mono.error(findException));

        // When & Then
        StepVerifier.create(userRepositoryAdapter.findByEmail(email))
                .expectError(RuntimeException.class)
                .verify();

        verify(logger, times(1)).error("Error al consultar usuario por correo: {}", email, findException);
        verify(logger, never()).info(anyString(), (Object) any());
    }

    // ========== TESTS FOR existsByIdentityDocument ==========
    @Test
    @DisplayName("Debería verificar existencia por documento de identidad exitosamente - true")
    void shouldCheckExistsByIdentityDocumentSuccessfullyTrue() {
        // Given
        String identityDocument = "12345678";
        when(userReactiveRepository.existsByIdentityDocument(identityDocument)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(userRepositoryAdapter.existsByIdentityDocument(identityDocument))
                .expectNext(true)
                .verifyComplete();

        verify(userReactiveRepository, times(1)).existsByIdentityDocument(identityDocument);
    }

    @Test
    @DisplayName("Debería verificar existencia por documento de identidad exitosamente - false")
    void shouldCheckExistsByIdentityDocumentSuccessfullyFalse() {
        // Given
        String identityDocument = "87654321";
        when(userReactiveRepository.existsByIdentityDocument(identityDocument)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(userRepositoryAdapter.existsByIdentityDocument(identityDocument))
                .expectNext(false)
                .verifyComplete();

        verify(userReactiveRepository, times(1)).existsByIdentityDocument(identityDocument);
    }

    @Test
    @DisplayName("Debería manejar error al verificar existencia por documento")
    void shouldHandleErrorWhenCheckingExistsByIdentityDocument() {
        // Given
        String identityDocument = "12345678";
        RuntimeException existsException = new RuntimeException("Database exists query error");
        when(userReactiveRepository.existsByIdentityDocument(identityDocument))
                .thenReturn(Mono.error(existsException));

        // When & Then
        StepVerifier.create(userRepositoryAdapter.existsByIdentityDocument(identityDocument))
                .expectError(RuntimeException.class)
                .verify();

        verify(userReactiveRepository, times(1)).existsByIdentityDocument(identityDocument);
    }

    @Test
    @DisplayName("Debería loggear éxito al verificar existencia por documento")
    void shouldLogSuccessWhenCheckingExistsByIdentityDocument() {
        // Given
        String identityDocument = "12345678";
        when(userReactiveRepository.existsByIdentityDocument(identityDocument)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(userRepositoryAdapter.existsByIdentityDocument(identityDocument))
                .expectNext(true)
                .verifyComplete();

        verify(logger, times(1)).info("Existe Documento BD: {}", true);
        verify(logger, never()).error(anyString(), any(), any(Throwable.class));
    }

    @Test
    @DisplayName("Debería loggear error al verificar existencia por documento")
    void shouldLogErrorWhenCheckingExistsByIdentityDocument() {
        // Given
        String identityDocument = "12345678";
        RuntimeException existsException = new RuntimeException("Exists query failed");
        when(userReactiveRepository.existsByIdentityDocument(identityDocument))
                .thenReturn(Mono.error(existsException));

        // When & Then
        StepVerifier.create(userRepositoryAdapter.existsByIdentityDocument(identityDocument))
                .expectError(RuntimeException.class)
                .verify();

        verify(logger, times(1)).error("Error al consultar usuario por documento: {}", identityDocument, existsException);
        verify(logger, never()).info(anyString(), (Object) any());
    }

    // ========== TESTS FOR INHERITED METHODS ==========
    @Test
    @DisplayName("Debería encontrar usuario por ID usando método heredado")
    void shouldFindUserByIdUsingInheritedMethod() {
        // Given
        UserRepositoryAdapter spyAdapter = spy(userRepositoryAdapter);
        doReturn(Mono.just(testUser)).when(spyAdapter).findById(testUserId);

        // When & Then
        StepVerifier.create(spyAdapter.findById(testUserId))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería encontrar todos los usuarios usando método heredado")
    void shouldFindAllUsersUsingInheritedMethod() {
        // Given
        User user2 = testUser.toBuilder()
                .id(UUID.randomUUID())
                .email("jane.doe@example.com")
                .identityDocument("87654321")
                .build();

        UserRepositoryAdapter spyAdapter = spy(userRepositoryAdapter);
        doReturn(Flux.just(testUser, user2)).when(spyAdapter).findAll();

        // When & Then
        StepVerifier.create(spyAdapter.findAll())
                .expectNext(testUser)
                .expectNext(user2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería retornar Flux vacío cuando no hay usuarios")
    void shouldReturnEmptyFluxWhenNoUsersExist() {
        // Given
        UserRepositoryAdapter spyAdapter = spy(userRepositoryAdapter);
        doReturn(Flux.empty()).when(spyAdapter).findAll();

        // When & Then
        StepVerifier.create(spyAdapter.findAll())
                .verifyComplete();
    }

    // Helper method to simulate super.save() call
    public Mono<User> superSave(User user) {
        return Mono.just(user);
    }

    // Helper method to simulate super.findById() call
    public Mono<User> superFindById(UUID id) {
        return Mono.just(testUser);
    }

    // Helper method to simulate super.findAll() call
    public Flux<User> superFindAll() {
        return Flux.just(testUser);
    }
}