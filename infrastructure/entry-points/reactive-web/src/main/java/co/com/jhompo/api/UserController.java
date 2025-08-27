package co.com.jhompo.api;

import co.com.jhompo.api.dtos.UserRequestDTO;
import co.com.jhompo.api.dtos.UserResponseDTO;
import co.com.jhompo.model.user.User;
import co.com.jhompo.usecase.user.UserUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UserController {

    private final UserUseCase userService;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserUseCase userService) {
        this.userService = userService;
    }


    @GetMapping("/{id}")
    public Mono<UserResponseDTO> findById(@PathVariable("id")  UUID id) {
        return userService.findById(id).map(UserResponseDTO::toDomain);
    }

    @GetMapping
    public Flux<User> findAll() {
        return userService.findAll();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDTO> createUser(@RequestBody UserRequestDTO dto) {
        log.info("*****Peticion POST para registrar un nuevo usuario recibida.");
        return userService.createUser(dto.toDomain())
                .map(UserResponseDTO::toDomain);
    }

    @PutMapping("/{id}")
    public Mono<UserResponseDTO> updateUser(@PathVariable(value = "id") UUID id, @RequestBody UserRequestDTO dto) {
        log.info("*****Petición PUT para actualizar usuario.");

        User user = dto.toDomain();
        user.setId(id);
        return userService.updateUser(user)
                .map(UserResponseDTO::toDomain)
                .doOnSuccess(u -> log.info("Actualizado en BD: {}", u))
                .doOnError(e -> log.error("Error al actualiar el usuario: {}", user.getEmail(), e));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable("id") UUID id) {
        log.info("*****Petición DELETE para eliminar un usuario.");
        return null; //userService.deleteById(id);
    }


    @GetMapping("/existe/{documentoIdentidad}")
    public Mono<Boolean> checkUserExists(@PathVariable("documentoIdentidad") String documentoIdentidad) {
        return userService.checkUserExistsByDocument(documentoIdentidad);
    }

    @GetMapping("/email/{email}")
    public Mono<Boolean> checkUserExistsByEmail(@PathVariable("email") String email) {
        return userService.checkUserExistsByEmail(email);
    }
}