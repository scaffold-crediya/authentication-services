package co.com.jhompo.api.handler;

import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import io.r2dbc.spi.R2dbcException;
import org.springframework.dao.DataIntegrityViolationException;
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
                    "Duplicate Key",
                    "Ya existe una solicitud con este email para el tipo de préstamo seleccionado.",
                    exchange.getRequest().getURI().getPath()
            );
        }

        if   (errorMessage != null && errorMessage.contains("fk_id_role")){
            return buildErrorResponse(
                    HttpStatus.CONFLICT,
                    "Validate Key",
                    "El Rol ingresado no existe",
                    exchange.getRequest().getURI().getPath()
            );
        }

        // Manejo genérico
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Data integrity violation",
                "El registro ya existe o viola una restricción de la base de datos.",
                exchange.getRequest().getURI().getPath()
        );
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

    @ExceptionHandler(R2dbcException.class)
    public ResponseEntity<Map<String, String>> handleR2dbcException(R2dbcException ex) {
        Map<String, String> body = Map.of(
                "error", "Database error",
                "message", "Error en la operación de base de datos: " + ex.getMessage()
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
                "Ocurrió un error inesperado. Por favor, inténtelo de nuevo más tarde.",
                exchange.getRequest().getPath().toString()
        );
    }
}