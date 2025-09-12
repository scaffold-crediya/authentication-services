package co.com.jhompo.api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static co.com.jhompo.common.Messages.JWT.BEARER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private WebFilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtProvider);
    }

    @Test
    void constructor_ShouldInitializeJwtProvider() {
        // Given & When
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider);

        // Then
        assertNotNull(filter);
    }

    @Test
    void filter_WithoutAuthorizationHeader_ShouldContinueChain() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(filterChain).filter(exchange);
        verifyNoInteractions(jwtProvider);
    }

    @Test
    void filter_WithInvalidAuthorizationHeader_ShouldContinueChain() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, "Invalid header")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(filterChain).filter(exchange);
        verifyNoInteractions(jwtProvider);
    }

    @Test
    void filter_WithValidTokenButInvalidValidation_ShouldContinueChain() {
        // Given
        String token = "valid.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtProvider.validateToken(token)).thenReturn(false);
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(jwtProvider).validateToken(token);
        verify(filterChain).filter(exchange);
        verify(jwtProvider, never()).getEmailFromToken(anyString());
        verify(jwtProvider, never()).getRolesFromToken(anyString());
    }

    @Test
    void filter_WithValidToken_ShouldSetAuthenticationAndContinueChain() {
        // Given
        String token = "valid.jwt.token";
        String email = "test@example.com";
        List<String> roles = Arrays.asList("ADMIN", "USER");

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtProvider.getRolesFromToken(token)).thenReturn(roles);
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // When
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(jwtProvider).validateToken(token);
        verify(jwtProvider).getEmailFromToken(token);
        verify(jwtProvider).getRolesFromToken(token);
        verify(filterChain).filter(any(ServerWebExchange.class));
    }

    @Test
    void filter_WithBearerTokenWithoutSpace_ShouldExtractTokenCorrectly() {
        // Given
        String token = "valid.jwt.token";
        String bearerToken = BEARER + token; // Sin espacio después de Bearer
        String email = "test@example.com";
        List<String> roles = Arrays.asList("ADMIN");

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtProvider.getRolesFromToken(token)).thenReturn(roles);
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // When
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(jwtProvider).validateToken(token);
    }

    @Test
    void filter_WithEmptyRolesList_ShouldSetAuthenticationWithEmptyAuthorities() {
        // Given
        String token = "valid.jwt.token";
        String email = "test@example.com";
        List<String> roles = Arrays.asList(); // Lista vacía

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .header(HttpHeaders.AUTHORIZATION, BEARER + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getEmailFromToken(token)).thenReturn(email);
        when(jwtProvider.getRolesFromToken(token)).thenReturn(roles);
        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());

        // When
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(jwtProvider).validateToken(token);
        verify(jwtProvider).getEmailFromToken(token);
        verify(jwtProvider).getRolesFromToken(token);
    }

    @Test
    void filter_WithNullAuthorizationHeader_ShouldContinueChain() {
        // Given
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/test")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        // When
        Mono<Void> result = jwtAuthenticationFilter.filter(exchange, filterChain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(filterChain).filter(exchange);
        verifyNoInteractions(jwtProvider);
    }
}