package co.com.jhompo.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityHeadersConfigTest {

    @Test
    void testSecurityHeadersAreSet() {
        SecurityHeadersConfig filter = new SecurityHeadersConfig();

        // Crear request y exchange simulado
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        MockServerHttpResponse response = exchange.getResponse();

        // Mock chain
        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        // Ejecutar el filtro
        filter.filter(exchange, chain).block();

        HttpHeaders headers = response.getHeaders();

        // Validaciones
        assertThat(headers.getFirst("Content-Security-Policy"))
                .isEqualTo("default-src 'self'; frame-ancestors 'self'; form-action 'self'");
        assertThat(headers.getFirst("Strict-Transport-Security"))
                .isEqualTo("max-age=31536000;");
        assertThat(headers.getFirst("X-Content-Type-Options"))
                .isEqualTo("nosniff");
        assertThat(headers.getFirst("Server"))
                .isEqualTo("");
        assertThat(headers.getFirst("Cache-Control"))
                .isEqualTo("no-store");
        assertThat(headers.getFirst("Pragma"))
                .isEqualTo("no-cache");
        assertThat(headers.getFirst("Referrer-Policy"))
                .isEqualTo("strict-origin-when-cross-origin");
    }
}
