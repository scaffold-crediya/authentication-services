package co.com.jhompo.api.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.AuthenticationException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAuthenticationEntryPointTest {

    private CustomAuthenticationEntryPoint entryPoint;

    @BeforeEach
    void setUp() {
        entryPoint = new CustomAuthenticationEntryPoint();
    }

    private String getResponseBody(MockServerWebExchange exchange) {
        return DataBufferUtils.join(exchange.getResponse().getBody())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .defaultIfEmpty("")
                .block();
    }

    @Test
    void commence_withGenericAuthException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/test").build());
        AuthenticationException ex = new AuthenticationException("generic") {};

        Mono<Void> result = entryPoint.commence(exchange, ex);

        StepVerifier.create(result).verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, exchange.getResponse().getHeaders().getContentType());

        String body = getResponseBody(exchange);
        // Verificación más flexible para evitar problemas de encoding
        assertTrue(body.contains("Token inv") && body.contains("lido"),
                "Response body should contain token invalid message, got: " + body);
    }

    @Test
    void commence_withExpiredJwtException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/test").build());
        ExpiredJwtException cause = mock(ExpiredJwtException.class);
        AuthenticationException ex = new AuthenticationException("expired", cause) {};

        Mono<Void> result = entryPoint.commence(exchange, ex);

        StepVerifier.create(result).verifyComplete();

        String body = getResponseBody(exchange);
        assertTrue(body.contains("Token expirado"),
                "Response body should contain expired token message, got: " + body);
    }

    @Test
    void commence_withSignatureException() throws Exception {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/test").build());
        SignatureException cause = new SignatureException("bad signature");
        AuthenticationException ex = new AuthenticationException("signature", cause) {};

        Mono<Void> result = entryPoint.commence(exchange, ex);

        StepVerifier.create(result).verifyComplete();

        String body = getResponseBody(exchange);
        assertNotNull(body, "Response body should not be null");
        assertFalse(body.isEmpty(), "Response body should not be empty");

        ObjectMapper mapper = new ObjectMapper();
        ErrorResponse errorResponse = mapper.readValue(body, ErrorResponse.class);

        // Verificación más flexible para el mensaje
        String message = errorResponse.message();
        assertTrue(message.contains("Token con firma inv"),
                "Expected message about invalid signature, got: " + message);

        assertEquals(401, errorResponse.status());
        assertEquals("Unauthorized", errorResponse.error());
    }
}