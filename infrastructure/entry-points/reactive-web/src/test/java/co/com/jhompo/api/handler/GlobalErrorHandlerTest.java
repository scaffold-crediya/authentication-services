package co.com.jhompo.api.handler;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import io.r2dbc.spi.R2dbcException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.test.StepVerifier;

import java.security.SignatureException;


import static org.junit.jupiter.api.Assertions.*;
import static co.com.jhompo.common.Messages.SYSTEM.*;
import static co.com.jhompo.common.Messages.USER.*;
import static co.com.jhompo.common.Messages.ROLE.*;

class GlobalErrorHandlerTest {

    private GlobalErrorHandler handler;
    private ServerHttpRequest request;
    private MockServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        handler = new GlobalErrorHandler();
        request = MockServerHttpRequest.get("/api/test").build();
        exchange = MockServerWebExchange.from((MockServerHttpRequest) request);
    }

    @Test
    void handleInvalidSignature() {
        StepVerifier.create(handler.handleInvalidSignature(new SignatureException("bad signature"), request))
                .assertNext(resp -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
                    assertEquals("Invalid token signature ", resp.getBody().error());
                })
                .verifyComplete();
    }

    @Test
    void handleMalformedToken() {
        StepVerifier.create(handler.handleMalformedToken(new MalformedJwtException("bad token"), request))
                .assertNext(resp -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
                    assertEquals("Malformed token ", resp.getBody().error());
                })
                .verifyComplete();
    }

    @Test
    void handleExpiredToken() {
        StepVerifier.create(handler.handleExpiredToken(new ExpiredJwtException(null, null, "expired"), request))
                .assertNext(resp -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
                    assertEquals("Token expired ", resp.getBody().error());
                })
                .verifyComplete();
    }

    @Test
    void handleJwtException() {
        StepVerifier.create(handler.handleJwtException(new JwtException("invalid jwt"), request))
                .assertNext(resp -> {
                    assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
                    assertEquals("Invalid token ", resp.getBody().error());
                })
                .verifyComplete();
    }

    @Test
    void handleIllegalArgumentException() {
        StepVerifier.create(handler.handleIllegalArgumentException(new IllegalArgumentException("bad arg"), exchange))
                .assertNext(resp -> {
                    assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
                    assertEquals("Bad Request", resp.getBody().error());
                })
                .verifyComplete();
    }

    @Test
    void handleDataIntegrity_emailConflict() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("application_email_idx");
        StepVerifier.create(handler.handleDataIntegrity(ex, exchange))
                .assertNext(resp -> {
                    assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
                    assertEquals(DUPLICATE_KEY_ERROR, resp.getBody().error());
                    assertEquals(EXIST_OR_INTEGRITY_VIOLATION, resp.getBody().message());
                })
                .verifyComplete();
    }

    @Test
    void handleDataIntegrity_roleNotFound() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("fk_id_role");
        StepVerifier.create(handler.handleDataIntegrity(ex, exchange))
                .assertNext(resp -> {
                    assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
                    assertEquals(VALIDATE_KEY_ERROR, resp.getBody().error());
                    assertEquals(ROL_NOT_FOUND, resp.getBody().message());
                })
                .verifyComplete();
    }

    @Test
    void handleR2dbcDataIntegrityViolation_documentExists() {
        R2dbcDataIntegrityViolationException ex = new R2dbcDataIntegrityViolationException("users_identity_document_key", "state", 0, (String) null);
        var resp = handler.handleDataIntegrity(ex);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(DUPLICATE_KEY_ERROR, resp.getBody().get(ERROR));
        assertEquals(DOCUMENT_EXISTS, resp.getBody().get(MESSAGE));
    }

    @Test
    void handleR2dbcDataIntegrityViolation_emailExists() {
        R2dbcDataIntegrityViolationException ex = new R2dbcDataIntegrityViolationException("users_email_key", "state", 0, (String) null);
        var resp = handler.handleDataIntegrity(ex);
        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertEquals(EMAIL_ALREADY_EXISTS, resp.getBody().get(MESSAGE));
    }

    @Test
    void handleR2dbcException() {
        R2dbcException ex = new R2dbcException("db error") {};
        var resp = handler.handleR2dbcException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertTrue(resp.getBody().get(MESSAGE).contains("db error"));
    }

    @Test
    void handleIllegalStateException() {
        StepVerifier.create(handler.handleIllegalStateException(new IllegalStateException("not found"), exchange))
                .assertNext(resp -> {
                    assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
                    assertEquals("Not Found", resp.getBody().error());
                })
                .verifyComplete();
    }

    @Test
    void handleGenericException() {
        StepVerifier.create(handler.handleGenericException(new Exception("boom"), exchange))
                .assertNext(resp -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
                    assertEquals("Internal Server Error", resp.getBody().error());
                    assertEquals(UNEXPECTED_ERROR, resp.getBody().message());
                })
                .verifyComplete();
    }
}
