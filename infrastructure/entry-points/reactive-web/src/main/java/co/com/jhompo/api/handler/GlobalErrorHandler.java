package co.com.jhompo.api.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import io.r2dbc.spi.R2dbcException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.Map;

import static co.com.jhompo.common.Messages.ROLE.*;
import static co.com.jhompo.common.Messages.SYSTEM.*;
import static co.com.jhompo.common.Messages.USER.*;


@ControllerAdvice
public class GlobalErrorHandler {


    //Metodo utilitario genérico para construir la respuesta de error
    private Mono<ResponseEntity<ErrorResponse>> buildErrorResponse( HttpStatus status, String error, String message, String path ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                status.value(),
                error,
                message,
                path
        );
        return Mono.just(new ResponseEntity<>(response, status));
    }

    @ExceptionHandler(SignatureException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidSignature(SignatureException ex, ServerHttpRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid token signature ", ex.getMessage(), request.getPath().toString());
    }

    @ExceptionHandler(MalformedJwtException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleMalformedToken(MalformedJwtException ex, ServerHttpRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Malformed token ", ex.getMessage(), request.getPath().toString());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleExpiredToken(ExpiredJwtException ex, ServerHttpRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Token expired ", ex.getMessage(), request.getPath().toString());
    }

    @ExceptionHandler(JwtException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleJwtException(JwtException ex, ServerHttpRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid token ", ex.getMessage(), request.getPath().toString());
    }



    // Maneja errores de datos incorrectos (ej. validaciones de campos)
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(IllegalArgumentException ex, ServerWebExchange exchange) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                exchange.getRequest().getPath().toString()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataIntegrity(
            DataIntegrityViolationException ex, ServerWebExchange exchange) {

        String errorMessage = ex.getMessage();

        if (errorMessage != null && errorMessage.contains("application_email_idx")) {
            return buildErrorResponse(
                    HttpStatus.CONFLICT,
                    DUPLICATE_KEY_ERROR,
                    EXIST_OR_INTEGRITY_VIOLATION,
                    exchange.getRequest().getURI().getPath()
            );
        }

        if   (errorMessage != null && errorMessage.contains("fk_id_role")){
            return buildErrorResponse(
                    HttpStatus.CONFLICT,
                    VALIDATE_KEY_ERROR,
                    ROL_NOT_FOUND,
                    exchange.getRequest().getURI().getPath()
            );
        }

        // Manejo genérico
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                INTEGRITY_VIOLATION,
                INTEGRITY_ERROR,
                exchange.getRequest().getURI().getPath()
        );
    }

    @ExceptionHandler(R2dbcDataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(R2dbcDataIntegrityViolationException ex) {
        String driverMessage = ex.getMessage();
        String userMessage = INTEGRITY_ERROR;

        if (driverMessage != null && driverMessage.contains("users_identity_document_key")) {
            userMessage = DOCUMENT_EXISTS;
        } else if (driverMessage != null && driverMessage.contains("users_email_key")) {
            userMessage = EMAIL_ALREADY_EXISTS;
        }

        Map<String, String> body = Map.of(
                ERROR, DUPLICATE_KEY_ERROR,
                MESSAGE, userMessage
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(R2dbcException.class)
    public ResponseEntity<Map<String, String>> handleR2dbcException(R2dbcException ex) {
        Map<String, String> body = Map.of(
                ERROR, DATABASE_ERROR,
                MESSAGE, OPERATION_DB_ERROR + ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalStateException(IllegalStateException ex, ServerWebExchange exchange) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                exchange.getRequest().getPath().toString()
        );
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                UNEXPECTED_ERROR,
                exchange.getRequest().getPath().toString()
        );
    }
}