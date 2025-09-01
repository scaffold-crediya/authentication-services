package co.com.jhompo.api;

import co.com.jhompo.api.dtos.AuthDTOs.LoginRequestDTO;
import co.com.jhompo.api.dtos.AuthDTOs.LoginResponseDTO;
import co.com.jhompo.api.security.JwtProvider;
import co.com.jhompo.model.user.User;
import co.com.jhompo.usecase.user.LoginUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private LoginUseCase loginUseCase;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private LoginController loginController;

    private LoginRequestDTO validRequest;
    private User testUser;
    private String testToken;

    @BeforeEach
    void setUp() {
        validRequest = new LoginRequestDTO("test@example.com", "password123");
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .firstName("James")
                .lastName("Rodriguez")
                .identityDocument("12345678")
                .baseSalary(new BigDecimal("50000"))
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();
        testToken = "mocked-jwt-token";
    }

    @Test
    @DisplayName("Debería retornar una respuesta de login con token cuando la autenticación es exitosa")
    void shouldReturnLoginResponseWhenAuthenticationIsSuccessful() {
        // Given
        when(loginUseCase.execute(anyString(), anyString())).thenReturn(Mono.just(testUser));
        when(jwtProvider.generateToken(any(User.class))).thenReturn(testToken);

        // When
        Mono<LoginResponseDTO> response = loginController.login(validRequest);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(loginResponse -> loginResponse.token().equals(testToken))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería propagar el error cuando la autenticación falla")
    void shouldPropagateErrorWhenAuthenticationFails() {
        // Given
        when(loginUseCase.execute(anyString(), anyString()))
                .thenReturn(Mono.error(new IllegalArgumentException("Authentication failed: Invalid credentials")));

        // When
        Mono<LoginResponseDTO> response = loginController.login(validRequest);

        // Then
        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Authentication failed: Invalid credentials"))
                .verify();
    }

    @Test
    @DisplayName("Debería manejar un error interno en el caso de uso")
    void shouldHandleUseCaseError() {
        // Given
        when(loginUseCase.execute(anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Error en el servicio de login")));

        // When
        Mono<LoginResponseDTO> response = loginController.login(validRequest);

        // Then
        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Error en el servicio de login"))
                .verify();
    }

    @Test
    @DisplayName("Debería fallar con email y contraseña nulos")
    void shouldFailWithNullEmailAndPassword() {
        // Given
        LoginRequestDTO nullRequest = new LoginRequestDTO(null, null);
        when(loginUseCase.execute(null, null))
                .thenReturn(Mono.error(new IllegalArgumentException("Email and password cannot be null")));

        // When
        Mono<LoginResponseDTO> response = loginController.login(nullRequest);

        // Then
        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Email and password cannot be null"))
                .verify();
    }
}