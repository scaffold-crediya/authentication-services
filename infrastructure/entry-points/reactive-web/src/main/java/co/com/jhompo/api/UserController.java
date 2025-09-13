package co.com.jhompo.api;

import co.com.jhompo.api.dtos.user.UserRequestDTO;
import co.com.jhompo.api.dtos.user.UserResponseDTO;
import co.com.jhompo.model.user.User;
import co.com.jhompo.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static co.com.jhompo.util.Messages.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = USER.TITLE, description = USER.DESCRIPTION)
public class UserController {

    private final UserUseCase userService;
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final ModelMapper mapper;

    public UserController(UserUseCase userService, ModelMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @Operation(summary = USER.DESCRIPTION_FIND_BY_ID)
    @GetMapping("/{id}")
    public Mono<UserResponseDTO> findById(@PathVariable("id") UUID id) {
        return userService.findById(id).map(UserResponseDTO::toDomain);
    }

    @Operation(summary = USER.DESCRIPTION_GET_ALL)
    @GetMapping
    public Flux<User> findAll() {
        return userService.findAll();
    }

    @Operation(summary = USER.DESCRIPTION_CREATE)
    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDTO> createUser(@RequestBody UserRequestDTO dto) {
        log.info(USER.DESCRIPTION_CREATE);
        return userService.createUser(dto.toDomain())
                .map(UserResponseDTO::toDomain)
                .doOnSuccess(u -> log.info(USER.CREATED_SUCCESS, u))
                .doOnError(e -> log.error(USER.CREATION_FAILED, e));
    }

    @Operation(summary = USER.DESCRIPTION_UPDATE)
    @PutMapping("/{id}")
    public Mono<UserResponseDTO> updateUser(@PathVariable(value = "id") UUID id, @RequestBody UserRequestDTO dto) {
        log.info(USER.DESCRIPTION_UPDATE);

        User user = dto.toDomainForUpdate(id);

        return userService.updateUser(user)
                .map(UserResponseDTO::toDomain)
                .doOnSuccess(u -> log.info(USER.UPDATED_SUCCESS, u))
                .doOnError(e -> log.error(USER.UPDATE_FAILED, user.getEmail(), e));
    }

    @Operation(summary = USER.DESCRIPTION_DELETE)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable("id") UUID id) {
        return userService.deleteById(id)
                .doOnSuccess(u -> log.info(USER.DELETED_SUCCESS, u))
                .doOnError(e -> log.error(USER.DELETE_FAILED, e));
    }

    @Operation(summary = USER.DESCRIPTION_FIND_BY_DOCUMENT)
    @GetMapping("/existe/{documentoIdentidad}")
    public Mono<Boolean> checkUserExists(@PathVariable("documentoIdentidad") String documentoIdentidad) {
        return userService.checkUserExistsByDocument(documentoIdentidad)
                .doOnSuccess(u -> log.info(REQUEST.PROCESSED_SUCCESS, u))
                .doOnError(e -> log.error(USER.NOT_FOUND, e));
    }

    @Operation(summary = USER.DESCRIPTION_FIND_BY_EMAIL)
    @GetMapping("/email/{email}")
    public Mono<UserResponseDTO> checkUserExistsByEmail(@PathVariable("email") String email) {
        return userService.checkUserExistsByEmail(email)
                .map(user -> mapper.map(user, UserResponseDTO.class))
                .doOnSuccess(u -> log.info(REQUEST.PROCESSED_SUCCESS, u))
                .doOnError(e -> log.error(USER.NOT_FOUND, e));
    }


    @Operation(summary = USER.DESCRIPTION_FIND_LIST_EMAIL )
    @PostMapping("/details-by-email")
    public Flux<UserResponseDTO> getUserDetailsByEmails(@RequestBody List<String> emails) {
        return userService.findDetailsByEmails(emails)
                .map(user -> mapper.map(user, UserResponseDTO.class));
    }
}