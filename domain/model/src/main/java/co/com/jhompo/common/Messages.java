package co.com.jhompo.common;


// CLASE GLOBAL DE MENSAJES
public final class Messages {

    // ============================================
    // MENSAJES DE JWT/SECURITY
    // ============================================
    public static final class JWT {
        public static final String INVALID_TOKEN = "Token JWT inválido";
        public static final String TOKEN_EXPIRED = "Token JWT expirado";
        public static final String TOKEN_MALFORMED = "Token JWT mal formado";
        public static final String TOKEN_SIGNATURE_INVALID = "Firma del token JWT inválida";
        public static final String TOKEN_GENERATION_ERROR = "Error al generar el token JWT";
        public static final String TOKEN_VALIDATION_SUCCESS = "Token JWT válido";
        public static final String TOKEN_NOT_PROVIDED = "Token JWT no proporcionado";
        public static final String UNAUTHORIZED_ACCESS = "Acceso no autorizado";
        public static final String FORBIDDEN_ACCESS = "Acceso prohibido";

        private JWT() {}
    }

    // ============================================
    // MENSAJES DE USUARIO
    // ============================================
    public static final class USER {
        // Éxito
        public static final String CREATED_SUCCESS = "Usuario creado exitosamente";
        public static final String UPDATED_SUCCESS = "Usuario actualizado exitosamente";
        public static final String DELETED_SUCCESS = "Usuario eliminado exitosamente";
        public static final String LOGIN_SUCCESS = "Inicio de sesión exitoso";
        public static final String LOGOUT_SUCCESS = "Cierre de sesión exitoso";
        public static final String PROFILE_RETRIEVED = "Perfil obtenido exitosamente";
        public static final String PASSWORD_CHANGED = "Contraseña cambiada exitosamente";

        // Errores
        public static final String NOT_FOUND = "Usuario no encontrado";
        public static final String ALREADY_EXISTS = "El usuario ya existe";
        public static final String EMAIL_ALREADY_EXISTS = "El email ya está registrado";
        public static final String INVALID_CREDENTIALS = "Credenciales inválidas";
        public static final String INACTIVE_USER = "Usuario inactivo";
        public static final String CREATION_FAILED = "Error al crear el usuario";
        public static final String UPDATE_FAILED = "Error al actualizar el usuario";
        public static final String DELETE_FAILED = "Error al eliminar el usuario";
        public static final String PASSWORD_CHANGE_FAILED = "Error al cambiar la contraseña";

        // Validaciones
        public static final String EMAIL_REQUIRED = "El email es requerido";
        public static final String EMAIL_INVALID = "Formato de email inválido";
        public static final String PASSWORD_REQUIRED = "La contraseña es requerida";
        public static final String PASSWORD_TOO_SHORT = "La contraseña debe tener al menos 8 caracteres";
        public static final String PASSWORD_TOO_WEAK = "La contraseña debe contener mayúsculas, minúsculas y números";
        public static final String NAME_REQUIRED = "El nombre es requerido";
        public static final String NAME_TOO_SHORT = "El nombre debe tener al menos 2 caracteres";
        public static final String ROLE_REQUIRED = "El rol es requerido";
        public static final String CURRENT_PASSWORD_INCORRECT = "La contraseña actual es incorrecta";

        private USER() {}
    }

    // ============================================
    // MENSAJES DE ROL
    // ============================================
    public static final class ROLE {
        // Éxito
        public static final String CREATED_SUCCESS = "Rol creado exitosamente";
        public static final String UPDATED_SUCCESS = "Rol actualizado exitosamente";
        public static final String DELETED_SUCCESS = "Rol eliminado exitosamente";
        public static final String RETRIEVED_SUCCESS = "Roles obtenidos exitosamente";

        // Errores
        public static final String NOT_FOUND = "Rol no encontrado";
        public static final String ALREADY_EXISTS = "El rol ya existe";
        public static final String NAME_ALREADY_EXISTS = "Ya existe un rol con ese nombre";
        public static final String CREATION_FAILED = "Error al crear el rol";
        public static final String UPDATE_FAILED = "Error al actualizar el rol";
        public static final String DELETE_FAILED = "Error al eliminar el rol";
        public static final String IN_USE = "No se puede eliminar el rol porque está en uso";

        // Validaciones
        public static final String NAME_REQUIRED = "El nombre del rol es requerido";
        public static final String NAME_TOO_SHORT = "El nombre del rol debe tener al menos 2 caracteres";
        public static final String DESCRIPTION_REQUIRED = "La descripción del rol es requerida";
        public static final String INVALID_ROLE_ID = "ID de rol inválido";

        private ROLE() {}
    }

    // ============================================
    // MENSAJES DE PERMISOS
    // ============================================
    public static final class PERMISSION {
        // Éxito
        public static final String ASSIGNED_SUCCESS = "Permiso asignado exitosamente";
        public static final String REVOKED_SUCCESS = "Permiso revocado exitosamente";
        public static final String RETRIEVED_SUCCESS = "Permisos obtenidos exitosamente";

        // Errores
        public static final String NOT_FOUND = "Permiso no encontrado";
        public static final String ALREADY_ASSIGNED = "El permiso ya está asignado";
        public static final String ASSIGNMENT_FAILED = "Error al asignar el permiso";
        public static final String REVOCATION_FAILED = "Error al revocar el permiso";
        public static final String INSUFFICIENT_PERMISSIONS = "Permisos insuficientes";

        private PERMISSION() {}
    }

    // ============================================
    // MENSAJES GENERALES/SISTEMA
    // ============================================
    public static final class SYSTEM {
        // Éxito
        public static final String OPERATION_SUCCESS = "Operación realizada exitosamente";
        public static final String DATA_RETRIEVED = "Datos obtenidos exitosamente";
        public static final String CACHE_CLEARED = "Caché limpiado exitosamente";

        // Errores
        public static final String INTERNAL_ERROR = "Error interno del servidor";
        public static final String DATABASE_ERROR = "Error de conexión a la base de datos";
        public static final String NETWORK_ERROR = "Error de conexión de red";
        public static final String SERVICE_UNAVAILABLE = "Servicio no disponible";
        public static final String TIMEOUT_ERROR = "Tiempo de espera agotado";
        public static final String VALIDATION_ERROR = "Error de validación";
        public static final String SERIALIZATION_ERROR = "Error de serialización";
        public static final String DESERIALIZATION_ERROR = "Error de deserialización";

        // Validaciones generales
        public static final String REQUIRED_FIELD = "Campo requerido";
        public static final String INVALID_FORMAT = "Formato inválido";
        public static final String INVALID_ID = "ID inválido";
        public static final String INVALID_DATE = "Fecha inválida";
        public static final String INVALID_RANGE = "Rango inválido";
        public static final String RESOURCE_NOT_FOUND = "Recurso no encontrado";
        public static final String DUPLICATE_ENTRY = "Entrada duplicada";

        private SYSTEM() {}
    }

    // ============================================
    // MENSAJES DE SOLICITUDES
    // ============================================
    public static final class REQUEST {
        // Éxito
        public static final String CREATED_SUCCESS = "Solicitud creada exitosamente";
        public static final String UPDATED_SUCCESS = "Solicitud actualizada exitosamente";
        public static final String PROCESSED_SUCCESS = "Solicitud procesada exitosamente";
        public static final String APPROVED_SUCCESS = "Solicitud aprobada exitosamente";
        public static final String REJECTED_SUCCESS = "Solicitud rechazada exitosamente";
        public static final String CANCELLED_SUCCESS = "Solicitud cancelada exitosamente";

        // Errores
        public static final String NOT_FOUND = "Solicitud no encontrada";
        public static final String ALREADY_PROCESSED = "La solicitud ya fue procesada";
        public static final String INVALID_STATUS = "Estado de solicitud inválido";
        public static final String CREATION_FAILED = "Error al crear la solicitud";
        public static final String UPDATE_FAILED = "Error al actualizar la solicitud";
        public static final String PROCESSING_FAILED = "Error al procesar la solicitud";

        // Validaciones
        public static final String TYPE_REQUIRED = "El tipo de solicitud es requerido";
        public static final String DESCRIPTION_REQUIRED = "La descripción es requerida";
        public static final String INVALID_REQUEST_TYPE = "Tipo de solicitud inválido";

        private REQUEST() {}
    }

    // ============================================
    // MENSAJES HTTP/API
    // ============================================
    public static final class HTTP {
        public static final String BAD_REQUEST = "Solicitud incorrecta";
        public static final String UNAUTHORIZED = "No autorizado";
        public static final String FORBIDDEN = "Prohibido";
        public static final String NOT_FOUND = "No encontrado";
        public static final String METHOD_NOT_ALLOWED = "Método no permitido";
        public static final String CONFLICT = "Conflicto";
        public static final String UNPROCESSABLE_ENTITY = "Entidad no procesable";
        public static final String INTERNAL_SERVER_ERROR = "Error interno del servidor";
        public static final String SERVICE_UNAVAILABLE = "Servicio no disponible";
        public static final String REQUEST_TIMEOUT = "Tiempo de solicitud agotado";

        private HTTP() {}
    }

    // Constructor privado para evitar instanciación
    private Messages() {
        throw new IllegalStateException("Utility class - No debe ser instanciada");
    }
}

// ============================================
// EJEMPLO DE USO EN USE CASE
// ============================================
/*
@Component
public class CreateUserUseCase {

    public User execute(CreateUserCommand command) {
        // Validaciones
        if (command.getEmail() == null || command.getEmail().trim().isEmpty()) {
            throw new ValidationException(Messages.USER.EMAIL_REQUIRED);
        }

        if (userRepository.findByEmail(command.getEmail()).isPresent()) {
            throw new BusinessException(Messages.USER.EMAIL_ALREADY_EXISTS);
        }

        try {
            User user = User.builder()
                    .email(command.getEmail())
                    .password(passwordEncoder.encode(command.getPassword()))
                    .roleId(command.getRoleId())
                    .build();

            User savedUser = userRepository.save(user);

            // Log o respuesta exitosa
            log.info(Messages.USER.CREATED_SUCCESS + " - Email: {}", user.getEmail());

            return savedUser;

        } catch (Exception e) {
            log.error(Messages.USER.CREATION_FAILED, e);
            throw new TechnicalException(Messages.USER.CREATION_FAILED);
        }
    }
}
*/

// ============================================
// EJEMPLO DE USO EN CONTROLLER
// ============================================
/*
@RestController
public class UserController {

    @PostMapping("/usuarios")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody CreateUserRequest request) {
        try {
            User user = createUserUseCase.execute(request.toCommand());

            return ResponseEntity.ok(ApiResponse.<User>builder()
                    .success(true)
                    .message(Messages.USER.CREATED_SUCCESS)
                    .data(user)
                    .build());

        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(ApiResponse.<User>builder()
                    .success(false)
                    .message(e.getMessage()) // Ya contiene el mensaje de Messages.USER.*
                    .build());
        }
    }
}
*/

// ============================================
// EJEMPLO DE USO EN JWT PROVIDER (TU ARCHIVO)
// ============================================
/*
public boolean validateToken(String token) {
    try {
        Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
        log.debug(Messages.JWT.TOKEN_VALIDATION_SUCCESS);
        return true;
    } catch (ExpiredJwtException e) {
        log.error(Messages.JWT.TOKEN_EXPIRED + ": {}", e.getMessage());
        return false;
    } catch (MalformedJwtException e) {
        log.error(Messages.JWT.TOKEN_MALFORMED + ": {}", e.getMessage());
        return false;
    } catch (SignatureException e) {
        log.error(Messages.JWT.TOKEN_SIGNATURE_INVALID + ": {}", e.getMessage());
        return false;
    } catch (JwtException | IllegalArgumentException e) {
        log.error(Messages.JWT.INVALID_TOKEN + ": {}", e.getMessage());
        return false;
    }
}
*/
