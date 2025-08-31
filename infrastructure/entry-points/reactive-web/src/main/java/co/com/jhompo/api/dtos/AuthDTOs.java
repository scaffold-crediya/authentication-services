package co.com.jhompo.api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthDTOs {
    @Schema(description = "Modelo de datos requerido para el inicio de sesión del usuario.")
    public record LoginRequestDTO(

            @Schema(description = "Dirección de correo electrónico registrada del usuario.",
                    example = "admin@tuempresa.com", requiredMode = Schema.RequiredMode.REQUIRED)
            String email,

            @Schema(description = "Contraseña del usuario.",
                    example = "contraseña123", requiredMode = Schema.RequiredMode.REQUIRED)
            String password
    ) {}

    @Schema(description = "Modelo de datos que contiene el token de autenticación.")
    public record LoginResponseDTO(

            @Schema(description = "Token Web JSON (JWT) para autenticar las solicitudes posteriores.")
            String token
    ) {}
}
