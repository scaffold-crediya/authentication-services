package co.com.jhompo.model.user.gateways;

import co.com.jhompo.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private User secondUser;
    private UUID testId;
    private UUID secondId;
    private String testEmail;
    private String testIdentityDocument;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        secondId = UUID.randomUUID();
        testEmail = "claudia.ramirez@crediya.com";
        testIdentityDocument = "43567890";

        testUser = User.builder()
                .id(testId)
                .firstName("Claudia")
                .lastName("Ramírez")
                .address("Calle 30 #15-45, Bocagrande, Cartagena")
                .phoneNumber("+57 302 345 6789")
                .email(testEmail)
                .baseSalary(new BigDecimal("3800000"))
                .birthDate(LocalDate.of(1992, 3, 15))
                .identityDocument(testIdentityDocument)
                .password("password2024")
                .roleId(1)
                .build();

        secondUser = User.builder()
                .id(secondId)
                .firstName("Fernando")
                .lastName("Vargas")
                .address("Carrera 50 #80-120, Barranquilla")
                .phoneNumber("+57 305 678 9012")
                .email("fernando.vargas@crediya.com")
                .baseSalary(new BigDecimal("4200000"))
                .birthDate(LocalDate.of(1985, 7, 22))
                .identityDocument("87654321")
                .password("fernandoPass123")
                .roleId(2)
                .build();
    }

    @Nested
    @DisplayName("Save Operation Tests")
    class SaveOperationTests {

        @Test
        @DisplayName("Should save user successfully")
        void shouldSaveUserSuccessfully() {
            // Given
            when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));

            // When & Then
            StepVerifier.create(userRepository.save(testUser))
                    .expectNext(testUser)
                    .verifyComplete();

            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should save user and return updated entity")
        void shouldSaveUserAndReturnUpdatedEntity() {
            // Given
            User userToSave = testUser.toBuilder().id(null).build();
            User savedUser = testUser.toBuilder().id(testId).build();

            when(userRepository.save(userToSave)).thenReturn(Mono.just(savedUser));

            // When & Then
            StepVerifier.create(userRepository.save(userToSave))
                    .expectNext(savedUser)
                    .verifyComplete();

            verify(userRepository).save(userToSave);
        }

        @Test
        @DisplayName("Should handle save operation with null user")
        void shouldHandleSaveOperationWithNullUser() {
            // Given
            when(userRepository.save(null)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(userRepository.save(null))
                    .verifyComplete();

            verify(userRepository).save(null);
        }

        @Test
        @DisplayName("Should handle save operation error")
        void shouldHandleSaveOperationError() {
            // Given
            RuntimeException saveError = new RuntimeException("Database connection failed");
            when(userRepository.save(any(User.class))).thenReturn(Mono.error(saveError));

            // When & Then
            StepVerifier.create(userRepository.save(testUser))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Should handle save operation with timeout")
        void shouldHandleSaveOperationWithTimeout() {
            // Given
            when(userRepository.save(any(User.class)))
                    .thenReturn(Mono.just(testUser).delayElement(Duration.ofSeconds(2)));

            // When & Then
            StepVerifier.create(userRepository.save(testUser))
                    .expectTimeout(Duration.ofSeconds(1))
                    .verify();

            verify(userRepository).save(testUser);
        }
    }

    @Nested
    @DisplayName("Find By ID Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find user by id successfully")
        void shouldFindUserByIdSuccessfully() {
            // Given
            when(userRepository.findById(testId)).thenReturn(Mono.just(testUser));

            // When & Then
            StepVerifier.create(userRepository.findById(testId))
                    .expectNext(testUser)
                    .verifyComplete();

            verify(userRepository).findById(testId);
        }

        @Test
        @DisplayName("Should return empty when user not found by id")
        void shouldReturnEmptyWhenUserNotFoundById() {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(userRepository.findById(nonExistentId)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(userRepository.findById(nonExistentId))
                    .verifyComplete();

            verify(userRepository).findById(nonExistentId);
        }

        @Test
        @DisplayName("Should handle findById with null parameter")
        void shouldHandleFindByIdWithNullParameter() {
            // Given
            when(userRepository.findById(null)).thenReturn(Mono.empty());

            // When & Then
            StepVerifier.create(userRepository.findById(null))
                    .verifyComplete();

            verify(userRepository).findById(null);
        }

        @Test
        @DisplayName("Should handle findById operation error")
        void shouldHandleFindByIdOperationError() {
            // Given
            RuntimeException findError = new RuntimeException("Query execution failed");
            when(userRepository.findById(testId)).thenReturn(Mono.error(findError));

            // When & Then
            StepVerifier.create(userRepository.findById(testId))
                    .expectError(RuntimeException.class)
                    .verify();

            verify(userRepository).findById(testId);
        }
    }
}