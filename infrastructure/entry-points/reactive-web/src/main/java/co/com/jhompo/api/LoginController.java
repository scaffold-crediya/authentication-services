package co.com.jhompo.api;

import co.com.jhompo.api.dtos.AuthDTOs.LoginRequestDTO;
import co.com.jhompo.api.dtos.AuthDTOs.LoginResponseDTO;
import co.com.jhompo.api.security.JwtProvider;
import co.com.jhompo.usecase.user.LoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints para el inicio de sesión y gestión de tokens.")
public class LoginController {

    private final LoginUseCase loginUseCase;
    private final JwtProvider jwtProvider;

    @Operation(
            summary = "Autenticar un usuario",
            description = "Recibe las credenciales del usuario (email y contraseña) y devuelve un token JWT si la autenticación es exitosa."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación Exitosa",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "No Autorizado - Credenciales inválidas", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error Interno del Servidor", content = @Content)
    })
    @PostMapping
    public Mono<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return loginUseCase.execute(request.email(), request.password())
                .map(user -> {
                    String token = jwtProvider.generateToken(user);
                    return new LoginResponseDTO(token);
                });
    }

}
