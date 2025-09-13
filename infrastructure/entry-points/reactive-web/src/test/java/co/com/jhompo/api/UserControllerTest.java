package co.com.jhompo.api;

import co.com.jhompo.api.dtos.user.UserRequestDTO;
import co.com.jhompo.api.dtos.user.UserResponseDTO;
import co.com.jhompo.model.user.User;
import co.com.jhompo.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserUseCase userUseCase;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;
    private final UUID testUserId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(testUserId)
                .firstName("James")
                .lastName("Rodriguez")
                .email("james.rodriguez@example.com")
                .password("hashedPassword")
                .identityDocument("123456789")
                .baseSalary(new BigDecimal("50000"))
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();

        userRequestDTO = UserRequestDTO.builder()
                .firstName("James")
                .lastName("Rodriguez")
                .email("james.rodriguez@example.com")
                .password("plainTextPassword")
                .identityDocument("123456789")
                .baseSalary(new BigDecimal("50000"))
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();

        userResponseDTO = UserResponseDTO.builder()
                .id(testUserId)
                .firstName("James")
                .lastName("Rodriguez")
                .email("james.rodriguez@example.com")
                .identityDocument("123456789")
                .baseSalary(new BigDecimal("50000"))
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();
    }

    @Test
    @DisplayName("Debería encontrar un usuario por ID")
    void shouldFindUserById() {
        // Given
        when(userUseCase.findById(testUserId)).thenReturn(Mono.just(testUser));

        // When & Then
        StepVerifier.create(userController.findById(testUserId))
                .expectNextMatches(dto -> dto.getId().equals(testUserId))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería retornar un Mono vacío si el usuario no es encontrado por ID")
    void shouldReturnEmptyMonoIfUserByIdDoesNotExist() {
        // Given
        when(userUseCase.findById(any(UUID.class))).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userController.findById(UUID.randomUUID()))
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("Debería listar todos los usuarios")
    void shouldFindAllUsers() {
        // Given
        when(userUseCase.findAll()).thenReturn(Flux.just(testUser));

        // When & Then
        StepVerifier.create(userController.findAll())
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería retornar un Flux vacío si no hay usuarios")
    void shouldReturnEmptyFluxIfNoUsers() {
        // Given
        when(userUseCase.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(userController.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería crear un nuevo usuario exitosamente")
    void shouldCreateNewUserSuccessfully() {
        // Given
        when(userUseCase.createUser(any(User.class))).thenReturn(Mono.just(testUser));

        // When & Then
        StepVerifier.create(userController.createUser(userRequestDTO))
                .expectNextMatches(dto -> dto.getId().equals(testUserId))
                .verifyComplete();
    }

   /* @Test
    @DisplayName("Debería propagar error al crear usuario")
    void shouldPropagateErrorWhenCreatingUser() {
        // Given
        when(userUseCase.createUser(any(User.class))).thenReturn(Mono.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(userController.createUser(userRequestDTO))
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("Database error"))
                .verify();
    }*/

    @Test
    @DisplayName("Debería actualizar un usuario exitosamente")
    void shouldUpdateUserSuccessfully() {
        // Given
        when(userUseCase.updateUser(any(User.class))).thenReturn(Mono.just(testUser));

        // When & Then
        StepVerifier.create(userController.updateUser(testUserId, userRequestDTO))
                .expectNextMatches(dto -> dto.getId().equals(testUserId))
                .verifyComplete();
    }


    @Test
    @DisplayName("Debería eliminar un usuario por ID")
    void shouldDeleteUserByIdSuccessfully() {
        // Given
        when(userUseCase.deleteById(testUserId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userController.deleteById(testUserId))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería verificar si un usuario existe por documento de identidad")
    void shouldCheckIfUserExistsByDocument() {
        // Given
        when(userUseCase.checkUserExistsByDocument(anyString())).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(userController.checkUserExists("123456789"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería verificar si el usuario no existe por documento de identidad")
    void shouldCheckIfUserDoesNotExistByDocument() {
        // Given
        when(userUseCase.checkUserExistsByDocument(anyString())).thenReturn(Mono.just(false));

        // When & Then
        StepVerifier.create(userController.checkUserExists("999999999"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería encontrar un usuario por email")
    void shouldFindUserByEmail() {
        // Given
        when(userUseCase.checkUserExistsByEmail(anyString())).thenReturn(Mono.just(testUser));
        when(mapper.map(any(User.class), any())).thenReturn(userResponseDTO);

        // When & Then
        StepVerifier.create(userController.checkUserExistsByEmail("james.rodriguez@example.com"))
                .expectNextMatches(dto -> dto.getEmail().equals("james.rodriguez@example.com"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería retornar un Mono vacío si el usuario no es encontrado por email")
    void shouldReturnEmptyMonoIfUserByEmailDoesNotExist() {
        // Given
        when(userUseCase.checkUserExistsByEmail(anyString())).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userController.checkUserExistsByEmail("nonexistent@example.com"))
                .expectComplete()
                .verify();
    }
}