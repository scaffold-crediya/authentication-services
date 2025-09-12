package co.com.jhompo.api.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private AccessDeniedException accessDeniedException;

    private CustomAccessDeniedHandler handler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        handler = new CustomAccessDeniedHandler();
        objectMapper = new ObjectMapper();
    }

    @Test
    void handle_ShouldSetCorrectHttpStatusAndContentType() {
        // Given
        when(exchange.getResponse()).thenReturn(response);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getPath()).thenReturn(mock(org.springframework.http.server.RequestPath.class));
        when(request.getPath().toString()).thenReturn("/api/test");
        when(response.getHeaders()).thenReturn(mock(org.springframework.http.HttpHeaders.class));

        DataBuffer dataBuffer = new DefaultDataBufferFactory().allocateBuffer();
        when(response.writeWith(any(Mono.class))).thenReturn(Mono.empty());

        // When
        Mono<Void> result = handler.handle(exchange, accessDeniedException);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(response.getHeaders()).setContentType(MediaType.APPLICATION_JSON);
    }


    @Test
    void handle_ShouldHandleNullPath() {
        // Given
        when(exchange.getResponse()).thenReturn(response);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getPath()).thenReturn(mock(org.springframework.http.server.RequestPath.class));
        when(request.getPath().toString()).thenReturn(null);
        when(response.getHeaders()).thenReturn(mock(org.springframework.http.HttpHeaders.class));
        when(response.writeWith(any(Mono.class))).thenReturn(Mono.empty());

        // When
        Mono<Void> result = handler.handle(exchange, accessDeniedException);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
        verify(response.getHeaders()).setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void handle_ShouldWorkWithDifferentPaths() {
        // Given
        String[] testPaths = {"/api/users", "/admin/settings", "/public/info", ""};

        for (String path : testPaths) {
            // Reset mocks
            reset(exchange, response, request);

            when(exchange.getResponse()).thenReturn(response);
            when(exchange.getRequest()).thenReturn(request);
            when(request.getPath()).thenReturn(mock(org.springframework.http.server.RequestPath.class));
            when(request.getPath().toString()).thenReturn(path);
            when(response.getHeaders()).thenReturn(mock(org.springframework.http.HttpHeaders.class));
            when(response.writeWith(any(Mono.class))).thenReturn(Mono.empty());

            // When
            Mono<Void> result = handler.handle(exchange, accessDeniedException);

            // Then
            StepVerifier.create(result)
                    .verifyComplete();

            verify(response).setStatusCode(HttpStatus.FORBIDDEN);
            verify(response.getHeaders()).setContentType(MediaType.APPLICATION_JSON);
        }
    }
}