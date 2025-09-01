package co.com.jhompo.usecase.user;

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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoderService passwordEncoder;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validUser;
    private UUID testUserId;
    private String hashedPassword;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        hashedPassword = "$2a$10$hashedPassword";

        validUser = User.builder()
                .id(testUserId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password123")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("100000"))
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();
    }

    // ========== TESTS FOR findById ==========
    @Test
    @DisplayName("Debería encontrar usuario por ID exitosamente")
    void shouldFindUserByIdSuccessfully() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Mono.just(validUser));

        // When & Then
        StepVerifier.create(userUseCase.findById(testUserId))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería retornar Mono vacío cuando el usuario no existe por ID")
    void shouldReturnEmptyMonoWhenUserNotFoundById() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userUseCase.findById(testUserId))
                .verifyComplete();
    }

    // ========== TESTS FOR findAll ==========
    @Test
    @DisplayName("Debería encontrar todos los usuarios exitosamente")
    void shouldFindAllUsersSuccessfully() {
        // Given
        User user2 = validUser.toBuilder()
                .id(UUID.randomUUID())
                .email("jane.doe@example.com")
                .identityDocument("87654321")
                .build();

        when(userRepository.findAll()).thenReturn(Flux.just(validUser, user2));

        // When & Then
        StepVerifier.create(userUseCase.findAll())
                .expectNext(validUser)
                .expectNext(user2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería retornar Flux vacío cuando no hay usuarios")
    void shouldReturnEmptyFluxWhenNoUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(userUseCase.findAll())
                .verifyComplete();
    }

    // ========== TESTS FOR createUser - SUCCESS ==========
    @Test
    @DisplayName("Debería crear usuario exitosamente con datos válidos")
    void shouldCreateUserSuccessfullyWithValidData() {
        // Given
        User userToSave = validUser.toBuilder().password(hashedPassword).build();

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userRepository.existsByIdentityDocument(anyString())).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn(Mono.just(hashedPassword));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(userToSave));

        // When & Then
        StepVerifier.create(userUseCase.createUser(validUser))
                .expectNext(userToSave)
                .verifyComplete();
    }

    // ========== TESTS FOR createUser - VALIDATION ERRORS ==========
    @Test
    @DisplayName("Debería fallar al crear usuario con firstName nulo")
    void shouldFailToCreateUserWithNullFirstName() {
        // Given
        User invalidUser = validUser.toBuilder().firstName(null).build();

        // When & Then
        StepVerifier.create(userUseCase.createUser(invalidUser))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El campo First Name es obligatorio."))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar al crear usuario con firstName vacío")
    void shouldFailToCreateUserWithBlankFirstName() {
        // Given
        User invalidUser = validUser.toBuilder().firstName("   ").build();

        // When & Then
        StepVerifier.create(userUseCase.createUser(invalidUser))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El campo First Name es obligatorio."))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar al crear usuario con lastName nulo")
    void shouldFailToCreateUserWithNullLastName() {
        // Given
        User invalidUser = validUser.toBuilder().lastName(null).build();

        // When & Then
        StepVerifier.create(userUseCase.createUser(invalidUser))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El campo Last Name es obligatorio."))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar al crear usuario con lastName vacío")
    void shouldFailToCreateUserWithBlankLastName() {
        // Given
        User invalidUser = validUser.toBuilder().lastName("").build();

        // When & Then
        StepVerifier.create(userUseCase.createUser(invalidUser))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El campo Last Name es obligatorio."))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar al crear usuario con email nulo")
    void shouldFailToCreateUserWithNullEmail() {
        // Given
        User invalidUser = validUser.toBuilder().email(null).build();

        // When & Then
        StepVerifier.create(userUseCase.createUser(invalidUser))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El campo Email es obligatorio."))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar al crear usuario con email vacío")
    void shouldFailToCreateUserWithBlankEmail() {
        // Given
        User invalidUser = validUser.toBuilder().email("").build();

        // When & Then
        StepVerifier.create(userUseCase.createUser(invalidUser))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El campo Email es obligatorio."))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar al crear usuario con formato de email inválido")
    void shouldFailToCreateUserWithInvalidEmailFormat() {
        // Given
        User invalidUser = validUser.toBuilder().email("invalid-email").build();

        // When & Then
        StepVerifier.create(userUseCase.createUser(invalidUser))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El formato del correo electrónico es inválido."))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar al crear usuario con salario nulo")
    void shouldFailToCreateUserWithNullBaseSalary() {
        // Given
        User invalidUser = validUser.toBuilder().baseSalary(null).build();

        // When & Then
        StepVerifier.create(userUseCase.createUser(invalidUser))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El campo Base Salary es obligatorio."))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar al crear usuario con salario menor o igual a cero")
    void shouldFailToCreateUserWithZeroBaseSalary() {
        // Given
        User invalidUser = validUser.toBuilder().baseSalary(BigDecimal.ZERO).build();

        // When & Then
        StepVerifier.create(userUseCase.createUser(invalidUser))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El salario base debe ser un valor numérico entre 0 y 1.500.000."))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar al crear usuario con salario mayor al límite")
    void shouldFailToCreateUserWithExcessiveBaseSalary() {
        // Given
        User invalidUser = validUser.toBuilder().baseSalary(new BigDecimal("1500001")).build();

        // When & Then
        StepVerifier.create(userUseCase.createUser(invalidUser))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El salario base debe ser un valor numérico entre 0 y 1.500.000."))
                .verify();
    }

    // ========== TESTS FOR createUser - BUSINESS LOGIC ERRORS ==========
    @Test
    @DisplayName("Debería fallar al crear usuario con email duplicado")
    void shouldFailToCreateUserWithDuplicateEmail() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(validUser));

        // When & Then
        StepVerifier.create(userUseCase.createUser(validUser))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El correo electrónico ya está registrado."))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar al crear usuario con documento de identidad duplicado")
    void shouldFailToCreateUserWithDuplicateIdentityDocument() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userRepository.existsByIdentityDocument(anyString())).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(userUseCase.createUser(validUser))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El documento de identidad ya está registrado."))
                .verify();
    }

    // ========== TESTS FOR updateUser ==========
    @Test
    @DisplayName("Debería actualizar usuario exitosamente")
    void shouldUpdateUserSuccessfully() {
        // Given
        User existingUser = validUser.toBuilder().firstName("OldName").build();
        User updatedUser = validUser.toBuilder().firstName("NewName").build();

        when(userRepository.findById(testUserId)).thenReturn(Mono.just(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(updatedUser));

        // When & Then
        StepVerifier.create(userUseCase.updateUser(validUser))
                .expectNext(updatedUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería fallar al actualizar usuario sin ID")
    void shouldFailToUpdateUserWithoutId() {
        // Given
        User userWithoutId = validUser.toBuilder().id(null).build();

        // When & Then
        StepVerifier.create(userUseCase.updateUser(userWithoutId))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("El ID del usuario es requerido para la actualización."))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar al actualizar usuario que no existe")
    void shouldFailToUpdateNonExistentUser() {
        // Given
        when(userRepository.findById(testUserId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userUseCase.updateUser(validUser))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("El usuario a actualizar no existe."))
                .verify();
    }

    // ========== TESTS FOR deleteById ==========
    @Test
    @DisplayName("Debería eliminar usuario por ID exitosamente")
    void shouldDeleteUserByIdSuccessfully() {
        // Given
        when(userRepository.deleteById(testUserId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userUseCase.deleteById(testUserId))
                .verifyComplete();
    }

    // ========== TESTS FOR checkUserExistsByDocument ==========
    @Test
    @DisplayName("Debería verificar existencia de usuario por documento exitosamente")
    void shouldCheckUserExistsByDocumentSuccessfully() {
        // Given
        when(userRepository.existsByIdentityDocument("12345678")).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(userUseCase.checkUserExistsByDocument("12345678"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería retornar false cuando el documento no existe")
    void shouldReturnFalseWhenDocumentNotExists() {
        // Given
        when(userRepository.existsByIdentityDocument("87654321")).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(userUseCase.checkUserExistsByDocument("87654321"))
                .expectNext(false)
                .verifyComplete();
    }

    // ========== TESTS FOR checkUserExistsByEmail ==========
    @Test
    @DisplayName("Debería encontrar usuario por email exitosamente")
    void shouldCheckUserExistsByEmailSuccessfully() {
        // Given
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Mono.just(validUser));

        // When & Then
        StepVerifier.create(userUseCase.checkUserExistsByEmail("john.doe@example.com"))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería retornar Mono vacío cuando el email no existe")
    void shouldReturnEmptyWhenEmailNotExists() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userUseCase.checkUserExistsByEmail("nonexistent@example.com"))
                .verifyComplete();
    }

    // ========== TESTS FOR EDGE CASES ==========
    @Test
    @DisplayName("Debería validar email con formato válido complejo")
    void shouldValidateComplexValidEmailFormat() {
        // Given
        User userWithComplexEmail = validUser.toBuilder()
                .email("test.user+tag@sub-domain.example-site.co.uk")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userRepository.existsByIdentityDocument(anyString())).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn(Mono.just(hashedPassword));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(userWithComplexEmail));

        // When & Then
        StepVerifier.create(userUseCase.createUser(userWithComplexEmail))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería aceptar salario en el límite máximo")
    void shouldAcceptMaximumBaseSalary() {
        // Given
        User userWithMaxSalary = validUser.toBuilder()
                .baseSalary(new BigDecimal("1500000"))
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userRepository.existsByIdentityDocument(anyString())).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn(Mono.just(hashedPassword));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(userWithMaxSalary));

        // When & Then
        StepVerifier.create(userUseCase.createUser(userWithMaxSalary))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería aceptar salario mínimo válido")
    void shouldAcceptMinimumValidBaseSalary() {
        // Given
        User userWithMinSalary = validUser.toBuilder()
                .baseSalary(new BigDecimal("0.01"))
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(userRepository.existsByIdentityDocument(anyString())).thenReturn(Mono.just(false));
        when(passwordEncoder.encode(anyString())).thenReturn(Mono.just(hashedPassword));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(userWithMinSalary));

        // When & Then
        StepVerifier.create(userUseCase.createUser(userWithMinSalary))
                .expectNextCount(1)
                .verifyComplete();
    }
}