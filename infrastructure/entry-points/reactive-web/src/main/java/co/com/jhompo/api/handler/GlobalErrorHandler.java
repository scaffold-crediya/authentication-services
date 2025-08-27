package co.com.jhompo.api.handler;

import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import io.r2dbc.spi.R2dbcException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalErrorHandler {

    // Maneja errores de datos incorrectos (ej. validaciones de campos)
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(IllegalArgumentException ex, ServerWebExchange exchange) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), // Tipo de error: "Bad Request"
                ex.getMessage(),
                exchange.getRequest().getPath().toString()
        );
        return Mono.just(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(R2dbcDataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(R2dbcDataIntegrityViolationException ex) {
        String driverMessage = ex.getMessage();
        String userMessage = "Se produjo un error de integridad de datos.";

        if (driverMessage != null && driverMessage.contains("users_identity_document_key")) {
            userMessage = "El documento de identidad ya existe en otro usuario.";
        } else if (driverMessage != null && driverMessage.contains("users_email_key")) {
            userMessage = "El correo electrónico ya está registrado.";
        }

        Map<String, String> body = Map.of(
                "error", "Duplicate key violation",
                "message", userMessage
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // También agrega este para capturar cualquier excepción R2DBC no manejada
    @ExceptionHandler(R2dbcException.class)
    public ResponseEntity<Map<String, String>> handleR2dbcException(R2dbcException ex) {
        Map<String, String> body = Map.of(
                "error", "Database error",
                "message", "Error en la operación de base de datos: " + ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // Maneja errores de conflictos de datos (ej. duplicados en la base de datos)
   /*  @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataIntegrityViolationException(DataIntegrityViolationException ex, ServerWebExchange exchange) {
        String userMessage = "Ha ocurrido un error de integridad de datos. Posiblemente, el registro que intentas crear ya existe.";
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(), // Tipo de error: "Conflict"
                userMessage,
                exchange.getRequest().getPath().toString()
        );
        return Mono.just(new ResponseEntity<>(response, HttpStatus.CONFLICT));
    } */

    // Maneja errores de estado ilegal (ej. recurso no encontrado)
    @ExceptionHandler(IllegalStateException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalStateException(IllegalStateException ex, ServerWebExchange exchange) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), // Tipo de error: "Not Found"
                ex.getMessage(),
                exchange.getRequest().getPath().toString()
        );
        return Mono.just(new ResponseEntity<>(response, HttpStatus.NOT_FOUND));
    }

    // Manejador genérico para cualquier otra excepción
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        String userMessage = "Ocurrió un error inesperado. Por favor, inténtelo de nuevo más tarde.";
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), // Tipo de error: "Internal Server Error"
                userMessage,
                exchange.getRequest().getPath().toString()
        );
        return Mono.just(new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
