package co.com.jhompo.usecase.user;

import co.com.jhompo.util.Messages.*;
import co.com.jhompo.model.security.PasswordEncoderService;
import co.com.jhompo.model.user.User;
import co.com.jhompo.model.user.gateways.UserRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de UserUseCase (reactivo)")
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoderService passwordEncoder;

    @InjectMocks
    private UserUseCase userUseCase;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Carlos")
                .lastName("Ramírez")
                .email("carlos.ramirez@example.com")
                .identityDocument("CC123456")
                .baseSalary(BigDecimal.valueOf(1_000_000))
                .birthDate(LocalDate.of(1990, 1, 1))
                .password("miClaveSegura")
                .build();
    }

    @Test
    @DisplayName("Debe actualizar un usuario correctamente")
    void testUpdateUser_Success() {
        // Usuario que llega en la petición (datos a actualizar)
        User incoming = testUser.toBuilder()
                .firstName("Carlos") // lo dejamos igual para el assert, podría cambiarse
                .lastName("Ramírez")
                .build();

        // Usuario existente en BD antes de actualizar (puede tener otros valores)
        User existing = User.builder()
                .id(incoming.getId())
                .firstName("María")
                .lastName("López")
                .email("maria.lopez@example.com")
                .identityDocument("CC999999")
                .baseSalary(BigDecimal.valueOf(900_000))
                .birthDate(LocalDate.of(1985, 5, 5))
                .password("hashed")
                .build();

        // Resultado esperado después de guardar (simulamos que save devuelve el usuario ya con cambios)
        User saved = existing.toBuilder()
                .firstName(incoming.getFirstName())
                .lastName(incoming.getLastName())
                .email(incoming.getEmail())
                .identityDocument(incoming.getIdentityDocument())
                .baseSalary(incoming.getBaseSalary())
                .birthDate(incoming.getBirthDate())
                .build();

        // Mocks: findById debe devolver el existente, save devuelve el guardado
        when(userRepository.findById(incoming.getId())).thenReturn(Mono.just(existing));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(saved));

        Mono<User> result = userUseCase.updateUser(incoming);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getFirstName().equals("Carlos")
                        && u.getId().equals(incoming.getId()))
                .verifyComplete();

        verify(userRepository, times(1)).findById(incoming.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe fallar al actualizar si el usuario no existe")
    void testUpdateUser_NotFound() {
        User incoming = testUser.toBuilder().firstName("Pepito").build();

        when(userRepository.findById(incoming.getId())).thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.updateUser(incoming))
                .expectErrorMatches(err -> err instanceof RuntimeException)
                .verify();

        verify(userRepository, times(1)).findById(incoming.getId());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe encontrar un usuario por su ID")
    void testFindById_Found() {
        UUID id = testUser.getId();
        when(userRepository.findById(id)).thenReturn(Mono.just(testUser));

        Mono<User> result = userUseCase.findById(id);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getEmail().equals("carlos.ramirez@example.com"))
                .verifyComplete();

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe completar vacío si no existe usuario para el ID")
    void testFindById_NotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Mono.empty());

        Mono<User> result = userUseCase.findById(id);

        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe eliminar un usuario por su ID")
    void testDeleteById() {
        UUID id = testUser.getId();
        when(userRepository.deleteById(id)).thenReturn(Mono.empty());

        Mono<Void> result = userUseCase.deleteById(id);

        StepVerifier.create(result).verifyComplete();

        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Debe validar si un usuario existe por documento (true)")
    void testCheckUserExistsByDocument_True() {
        when(userRepository.existsByIdentityDocument("CC123456")).thenReturn(Mono.just(true));

        Mono<Boolean> result = userUseCase.checkUserExistsByDocument("CC123456");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(userRepository, times(1)).existsByIdentityDocument("CC123456");
    }

    @Test
    @DisplayName("Debe validar si un usuario existe por documento (false)")
    void testCheckUserExistsByDocument_False() {
        when(userRepository.existsByIdentityDocument("NOEXIST")).thenReturn(Mono.just(false));

        Mono<Boolean> result = userUseCase.checkUserExistsByDocument("NOEXIST");

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(userRepository, times(1)).existsByIdentityDocument("NOEXIST");
    }

    @Test
    @DisplayName("Debe crear un usuario exitosamente")
    void testCreateUser_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.empty());
        when(userRepository.existsByIdentityDocument(testUser.getIdentityDocument())).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(testUser.getPassword())).thenReturn(Mono.just("hashedPassword"));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser.toBuilder().password("hashedPassword").build()));

        Mono<User> result = userUseCase.createUser(testUser);

        StepVerifier.create(result)
                .expectNextMatches(savedUser ->
                        savedUser.getEmail().equals(testUser.getEmail()) &&
                                savedUser.getPassword().equals("hashedPassword"))
                .verifyComplete();

        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
        verify(userRepository, times(1)).existsByIdentityDocument(testUser.getIdentityDocument());
        verify(passwordEncoder, times(1)).encode(testUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Debe fallar al crear usuario porque el correo ya existe")
    void testCreateUser_EmailAlreadyExists() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.just(testUser));

        Mono<User> result = userUseCase.createUser(testUser);

        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().equals(USER.EMAIL_ALREADY_EXISTS))
                .verify();

        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe fallar al crear usuario porque el documento ya existe")
    void testCreateUser_DocumentAlreadyExists() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.empty());
        when(userRepository.existsByIdentityDocument(testUser.getIdentityDocument())).thenReturn(Mono.just(true));

        Mono<User> result = userUseCase.createUser(testUser);

        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex instanceof IllegalArgumentException &&
                        ex.getMessage().equals(USER.DOCUMENT_EXISTS))
                .verify();

        verify(userRepository, times(1)).findByEmail(testUser.getEmail());
        verify(userRepository, times(1)).existsByIdentityDocument(testUser.getIdentityDocument());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe validar si un usuario existe por email (true)")
    void testCheckUserExistsByEmail_True() {
        String email = "carlos.ramirez@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Mono.just(testUser));

        Mono<User> result = userUseCase.checkUserExistsByEmail(email);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getEmail().equals(email))
                .verifyComplete();

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Debe validar si un usuario existe por email (false)")
    void testCheckUserExistsByEmail_False() {
        String email = "no@existe.com";

        when(userRepository.findByEmail(email)).thenReturn(Mono.empty());

        Mono<User> result = userUseCase.checkUserExistsByEmail(email);

        StepVerifier.create(result)
                .verifyComplete(); // no llega ningún User porque está vacío

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Debe buscar detalles de usuarios por lista de correos")
    void testFindDetailsByEmails() {
        List<String> emails = Arrays.asList("carlos.ramirez@example.com", "ana.gomez@example.com");
        List<User> users = Arrays.asList(
                testUser,
                User.builder()
                        .id(UUID.randomUUID())
                        .firstName("Ana")
                        .lastName("Gómez")
                        .email("ana.gomez@example.com")
                        .identityDocument("TI987654")
                        .baseSalary(BigDecimal.valueOf(900_000))
                        .birthDate(LocalDate.of(1992, 3, 3))
                        .build()
        );

        when(userRepository.findByEmailIn(emails)).thenReturn(Flux.fromIterable(users));

        Flux<User> result = userUseCase.findDetailsByEmails(emails);

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getFirstName().equals("Carlos"))
                .expectNextMatches(u -> u.getFirstName().equals("Ana"))
                .verifyComplete();

        verify(userRepository, times(1)).findByEmailIn(emails);
    }

    @Test
    @DisplayName("Debe listar todos los usuarios")
    void testFindAll() {
        List<User> users = Arrays.asList(
                testUser,
                User.builder()
                        .id(UUID.randomUUID())
                        .firstName("María")
                        .lastName("López")
                        .email("maria.lopez@example.com")
                        .identityDocument("CC789456")
                        .baseSalary(BigDecimal.valueOf(800_000))
                        .birthDate(LocalDate.of(1991, 7, 7))
                        .build()
        );

        when(userRepository.findAll()).thenReturn(Flux.fromIterable(users));

        Flux<User> result = userUseCase.findAll();

        StepVerifier.create(result)
                .expectNextMatches(u -> u.getFirstName().equals("Carlos"))
                .expectNextMatches(u -> u.getFirstName().equals("María"))
                .verifyComplete();

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe fallar cuando el nombre es nulo o vacío")
    void shouldFailWhenFirstNameIsNullOrBlank() {
        User invalidUser = testUser.toBuilder().firstName(null).build();

        StepVerifier.create(userUseCase.validateUser(invalidUser))
                .expectErrorSatisfies(error ->
                        assertTrue(error instanceof IllegalArgumentException &&
                                error.getMessage().equals(USER.NAME_REQUIRED))
                )
                .verify();

        invalidUser = testUser.toBuilder().firstName("   ").build();

        StepVerifier.create(userUseCase.validateUser(invalidUser))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals(USER.NAME_REQUIRED))
                .verify();
    }


    @Test
    @DisplayName("Debe fallar cuando el apellido es nulo o vacío")
    void shouldFailWhenLastNameIsNullOrBlank() {
        User invalidUser = testUser.toBuilder().lastName("").build();

        StepVerifier.create(userUseCase.validateUser(invalidUser))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals(USER.LASTNAME_REQUIRED))
                .verify();
    }

    @Test
    @DisplayName("Debe fallar cuando el salario base es <= 0 o > 1500000")
    void shouldFailWhenBaseSalaryIsOutOfRange() {
        User invalidUser1 = testUser.toBuilder().baseSalary(BigDecimal.ZERO).build();

        StepVerifier.create(userUseCase.validateUser(invalidUser1))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals(USER.SALARY_BASE_RULE))
                .verify();

        User invalidUser2 = testUser.toBuilder().baseSalary(new BigDecimal("2000000")).build();

        StepVerifier.create(userUseCase.validateUser(invalidUser2))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals(USER.SALARY_BASE_RULE))
                .verify();
    }

    @Test
    @DisplayName("Debe fallar cuando el email es nulo o vacío")
    void shouldFailWhenEmailIsNullOrBlank() {
        User invalidUser = testUser.toBuilder().email(null).build();

        StepVerifier.create(userUseCase.validateUser(invalidUser))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals(USER.EMAIL_REQUIRED))
                .verify();
    }


    @Test
    @DisplayName("Debe fallar cuando el email es inválido")
    void shouldFailWhenEmailIsInvalid() {
        User invalidUser = testUser.toBuilder().email("correo-invalido").build();

        StepVerifier.create(userUseCase.validateUser(invalidUser))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals(USER.EMAIL_INVALID))
                .verify();
    }

    @Test
    @DisplayName("Debe fallar cuando el salario base es nulo")
    void shouldFailWhenBaseSalaryIsNull() {
        User invalidUser = testUser.toBuilder().baseSalary(null).build();

        StepVerifier.create(userUseCase.validateUser(invalidUser))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().equals(USER.SALARY_BASE_REQUIRED))
                .verify();
    }

}
