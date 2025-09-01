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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoderService passwordEncoder;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User testUser;
    private String testEmail;
    private String testPassword;
    private String hashedPassword;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testPassword = "password123";
        hashedPassword = "$2a$10$hashedPassword";

        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email(testEmail)
                .password(hashedPassword)
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("50000"))
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();
    }

    @Test
    @DisplayName("Debería autenticar exitosamente con credenciales válidas")
    void shouldAuthenticateSuccessfullyWithValidCredentials() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches(testPassword, hashedPassword)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(loginUseCase.execute(testEmail, testPassword))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando el usuario no es encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(loginUseCase.execute(testEmail, testPassword))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Authentication failed: User not found"))
                .verify();
    }

    @Test
    @DisplayName("Debería lanzar excepción cuando la contraseña es incorrecta")
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches(testPassword, hashedPassword)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(loginUseCase.execute(testEmail, testPassword))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Authentication failed: Invalid credentials"))
                .verify();
    }

    @Test
    @DisplayName("Debería manejar error en el servicio de codificación de contraseñas")
    void shouldHandlePasswordEncoderServiceError() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Password encoding service error")));

        // When & Then
        StepVerifier.create(loginUseCase.execute(testEmail, testPassword))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Password encoding service error"))
                .verify();
    }

    @Test
    @DisplayName("Debería manejar error en el repositorio de usuarios")
    void shouldHandleUserRepositoryError() {
        // Given
        when(userRepository.findByEmail(testEmail))
                .thenReturn(Mono.error(new RuntimeException("Database connection error")));

        // When & Then
        StepVerifier.create(loginUseCase.execute(testEmail, testPassword))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database connection error"))
                .verify();
    }

    @Test
    @DisplayName("Debería autenticar con email en mayúsculas")
    void shouldAuthenticateWithUppercaseEmail() {
        // Given
        String uppercaseEmail = "TEST@EXAMPLE.COM";
        when(userRepository.findByEmail(uppercaseEmail)).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches(testPassword, hashedPassword)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(loginUseCase.execute(uppercaseEmail, testPassword))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería fallar con contraseña vacía")
    void shouldFailWithEmptyPassword() {
        // Given
        String emptyPassword = "";
        when(userRepository.findByEmail(testEmail)).thenReturn(Mono.just(testUser));
        when(passwordEncoder.matches(emptyPassword, hashedPassword)).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(loginUseCase.execute(testEmail, emptyPassword))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Authentication failed: Invalid credentials"))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar con email vacío")
    void shouldFailWithEmptyEmail() {
        // Given
        String emptyEmail = "";
        when(userRepository.findByEmail(emptyEmail)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(loginUseCase.execute(emptyEmail, testPassword))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Authentication failed: User not found"))
                .verify();
    }
}
