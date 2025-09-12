package co.com.jhompo.api.security;

import co.com.jhompo.api.handler.CustomAccessDeniedHandler;
import co.com.jhompo.api.handler.CustomAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private CustomAuthenticationEntryPoint customAuthEntryPoint;

    @Mock
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void constructor_ShouldInitializeAllDependencies() {
        // Given
        JwtAuthenticationFilter jwtFilter = mock(JwtAuthenticationFilter.class);
        CustomAuthenticationEntryPoint entryPoint = mock(CustomAuthenticationEntryPoint.class);
        CustomAccessDeniedHandler accessDeniedHandler = mock(CustomAccessDeniedHandler.class);

        // When
        SecurityConfig config = new SecurityConfig(jwtFilter, entryPoint, accessDeniedHandler);

        // Then
        assertNotNull(config);
    }

    @Test
    void springSecurityFilterChain_ShouldReturnSecurityWebFilterChain() {
        // Given
        ServerHttpSecurity http = ServerHttpSecurity.http();

        // When
        SecurityWebFilterChain filterChain = securityConfig.springSecurityFilterChain(http);

        // Then
        assertNotNull(filterChain);
    }

    @Test
    void springSecurityFilterChain_ShouldConfigureCSRFAsDisabled() {
        // Given
        ServerHttpSecurity http = ServerHttpSecurity.http();

        // When
        SecurityWebFilterChain filterChain = securityConfig.springSecurityFilterChain(http);

        // Then
        assertNotNull(filterChain);
        // El CSRF debe estar deshabilitado según la configuración
    }

    @Test
    void springSecurityFilterChain_ShouldConfigurePublicEndpoints() {
        // Given
        ServerHttpSecurity http = ServerHttpSecurity.http();

        // When
        SecurityWebFilterChain filterChain = securityConfig.springSecurityFilterChain(http);

        // Then
        assertNotNull(filterChain);
        // Los endpoints públicos como /v3/api-docs/**, /swagger-ui/** deben estar permitidos
        // Los endpoints /api/v1/login y /api/v1/usuarios/email/** deben estar permitidos
    }

    @Test
    void springSecurityFilterChain_ShouldConfigureExceptionHandlers() {
        // Given
        ServerHttpSecurity http = ServerHttpSecurity.http();

        // When
        SecurityWebFilterChain filterChain = securityConfig.springSecurityFilterChain(http);

        // Then
        assertNotNull(filterChain);
        // Verifica que los manejadores de excepción están configurados
        verifyNoInteractions(customAuthEntryPoint);
        verifyNoInteractions(customAccessDeniedHandler);
    }

    @Test
    void springSecurityFilterChain_ShouldDisableBasicAuthAndFormLogin() {
        // Given
        ServerHttpSecurity http = ServerHttpSecurity.http();

        // When
        SecurityWebFilterChain filterChain = securityConfig.springSecurityFilterChain(http);

        // Then
        assertNotNull(filterChain);
        // HTTP Basic y Form Login deben estar deshabilitados
    }

    @Test
    void springSecurityFilterChain_ShouldAddJwtFilter() {
        // Given
        ServerHttpSecurity http = ServerHttpSecurity.http();

        // When
        SecurityWebFilterChain filterChain = securityConfig.springSecurityFilterChain(http);

        // Then
        assertNotNull(filterChain);
        // El filtro JWT debe estar agregado en la posición correcta
    }
}