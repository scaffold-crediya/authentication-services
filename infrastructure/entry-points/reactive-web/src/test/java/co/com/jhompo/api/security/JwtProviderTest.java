package co.com.jhompo.api.security;

import co.com.jhompo.model.role.Role;
import co.com.jhompo.model.role.gateways.RoleRepository;
import co.com.jhompo.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static co.com.jhompo.util.Messages.ROLE.ROLE;
import static co.com.jhompo.util.Messages.ROLE.ROLE_ID;
import static co.com.jhompo.util.Messages.ROLE.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    @Mock
    private RoleRepository roleRepository;

    private JwtProvider jwtProvider;

    private final String secretKey = "dGhpc0lzQVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JKV1RUZXN0aW5nVGhhdElzTG9uZ0Vub3VnaDEyMw==";
    private final long expirationTime = 3600000L; // 1 hora

    private User testUser;
    private Role testRole;
    private UUID testUserId;
    private Integer testRoleId;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(roleRepository, secretKey, expirationTime);

        // Crear objetos de prueba
        testUserId = UUID.randomUUID();
        testRoleId = 1;

        testUser = User.builder()
                .id(testUserId)
                .email("test@example.com")
                .roleId(testRoleId)
                .build();

        testRole = Role.builder()
                .id(testRoleId)
                .name("ADMIN")
                .build();
    }

    @Test
    void constructor_ShouldInitializeFieldsCorrectly() {
        // Given & When
        JwtProvider provider = new JwtProvider(roleRepository, secretKey, expirationTime);

        // Then
        assertThat(provider).isNotNull();
    }

    @Test
    void generateToken_ShouldCreateValidToken_WhenUserIsValid() {
        // Given
        when(roleRepository.findById(testRoleId)).thenReturn(Mono.just(testRole));

        // When
        String token = jwtProvider.generateToken(testUser);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT tiene 3 partes separadas por puntos

        verify(roleRepository).findById(testRoleId);
    }

    @Test
    void generateToken_ShouldIncludeCorrectClaims_WhenUserIsValid() {
        // Given
        when(roleRepository.findById(testRoleId)).thenReturn(Mono.just(testRole));

        // When
        String token = jwtProvider.generateToken(testUser);

        // Then
        Claims claims = extractClaims(token);
        assertThat(claims.getSubject()).isEqualTo("test@example.com");
        assertThat(claims.get(ROLE, String.class)).isEqualTo("ADMIN");
        assertThat(claims.get(ROLE_ID, Integer.class)).isEqualTo(testRoleId);
        assertThat(claims.get(USER_ID, String.class)).isEqualTo(testUserId.toString());
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
    }

    @Test
    void generateToken_ShouldSetCorrectExpiration_WhenCalled() {
        // Given
        when(roleRepository.findById(testRoleId)).thenReturn(Mono.just(testRole));
        long beforeGeneration = System.currentTimeMillis();

        // When
        String token = jwtProvider.generateToken(testUser);

        // Then
        Claims claims = extractClaims(token);
        long tokenExpiration = claims.getExpiration().getTime();
        long expectedExpiration = beforeGeneration + expirationTime;

        // Permitir una pequeña diferencia de tiempo (1 segundo)
        assertThat(tokenExpiration).isBetween(expectedExpiration - 1000, expectedExpiration + 1000);
    }

    @Test
    void generateToken_ShouldConvertRoleNameToUpperCase_WhenRoleNameIsLowerCase() {
        // Given
        Role lowerCaseRole = Role.builder()
                .id(testRoleId)
                .name("admin")
                .build();
        when(roleRepository.findById(testRoleId)).thenReturn(Mono.just(lowerCaseRole));

        // When
        String token = jwtProvider.generateToken(testUser);

        // Then
        Claims claims = extractClaims(token);
        assertThat(claims.get(ROLE, String.class)).isEqualTo("ADMIN");
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        // Given
        when(roleRepository.findById(testRoleId)).thenReturn(Mono.just(testRole));
        String validToken = jwtProvider.generateToken(testUser);

        // When
        boolean isValid = jwtProvider.validateToken(validToken);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsInvalid() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtProvider.validateToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsEmpty() {
        // Given
        String emptyToken = "";

        // When
        boolean isValid = jwtProvider.validateToken(emptyToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsNull() {
        // Given
        String nullToken = null;

        // When
        boolean isValid = jwtProvider.validateToken(nullToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsExpired() {
        // Given
        JwtProvider shortExpirationProvider = new JwtProvider(roleRepository, secretKey, 1L); // 1ms
        when(roleRepository.findById(testRoleId)).thenReturn(Mono.just(testRole));
        String expiredToken = shortExpirationProvider.generateToken(testUser);

        // Esperar a que expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        boolean isValid = jwtProvider.validateToken(expiredToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    void getEmailFromToken_ShouldReturnCorrectEmail_WhenTokenIsValid() {
        // Given
        when(roleRepository.findById(testRoleId)).thenReturn(Mono.just(testRole));
        String token = jwtProvider.generateToken(testUser);

        // When
        String email = jwtProvider.getEmailFromToken(token);

        // Then
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    void getEmailFromToken_ShouldThrowException_WhenTokenIsInvalid() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> jwtProvider.getEmailFromToken(invalidToken));
    }

    @Test
    void getRolesFromToken_ShouldReturnCorrectRoles_WhenTokenIsValid() {
        // Given
        when(roleRepository.findById(testRoleId)).thenReturn(Mono.just(testRole));
        String token = jwtProvider.generateToken(testUser);

        // When
        List<String> roles = jwtProvider.getRolesFromToken(token);

        // Then
        assertThat(roles).hasSize(1);
        assertThat(roles).contains("ADMIN");
    }

    @Test
    void getRolesFromToken_ShouldReturnEmptyList_WhenRoleClaimIsNull() {
        // Given
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationTime);

        String tokenWithoutRole = Jwts.builder()
                .setSubject("test@example.com")
                .claim(ROLE_ID, testRoleId)
                .claim(USER_ID, testUserId.toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        // When
        List<String> roles = jwtProvider.getRolesFromToken(tokenWithoutRole);

        // Then
        assertThat(roles).isEmpty();
    }

    @Test
    void getRolesFromToken_ShouldThrowException_WhenTokenIsInvalid() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> jwtProvider.getRolesFromToken(invalidToken));
    }

    @Test
    void generateToken_ShouldHandleSpecialCharactersInEmail() {
        // Given
        UUID specialUserId = UUID.randomUUID();
        User userWithSpecialEmail = User.builder()
                .id(specialUserId)
                .email("test+special@example-domain.com")
                .roleId(testRoleId)
                .build();
        when(roleRepository.findById(testRoleId)).thenReturn(Mono.just(testRole));

        // When
        String token = jwtProvider.generateToken(userWithSpecialEmail);
        String extractedEmail = jwtProvider.getEmailFromToken(token);

        // Then
        assertThat(extractedEmail).isEqualTo("test+special@example-domain.com");
    }

    @Test
    void generateToken_ShouldHandleRoleWithSpaces() {
        // Given
        Role roleWithSpaces = Role.builder()
                .id(testRoleId)
                .name("super admin")
                .build();
        when(roleRepository.findById(testRoleId)).thenReturn(Mono.just(roleWithSpaces));

        // When
        String token = jwtProvider.generateToken(testUser);

        // Then
        Claims claims = extractClaims(token);
        assertThat(claims.get(ROLE, String.class)).isEqualTo("SUPER ADMIN");
    }

    // Métodos auxiliares
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }
}