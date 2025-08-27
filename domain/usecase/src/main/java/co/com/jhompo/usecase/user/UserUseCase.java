package co.com.jhompo.usecase.user;

import co.com.jhompo.model.user.User;
import co.com.jhompo.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Pattern;


@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");


    public Mono<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> createUser(User user) {
        return validateUser(user)
                .flatMap(validatedUser ->
                        userRepository.existsByEmail(validatedUser.getEmail())
                        .flatMap(emailExists -> {
                            if (emailExists) {
                                return Mono.error(new IllegalArgumentException("El correo electrónico ya está registrado."));
                            }
                            return userRepository.existsByIdentityDocument(validatedUser.getIdentityDocument())
                                    .flatMap(docExists -> {
                                        if (docExists) {
                                            return Mono.error(new IllegalArgumentException("El documento de identidad ya está registrado."));
                                        }
                                        return Mono.just(validatedUser);
                                    });
                        }))
                .flatMap(userRepository::save);
    }

    public Mono<User> updateUser(User user) {
        if (user.getId() == null) {
            return Mono.error(new IllegalArgumentException("El ID del usuario es requerido para la actualización."));
        }
        return validateUser(user)
                .then(userRepository.findById(user.getId()))
                .switchIfEmpty(Mono.error(new RuntimeException("El usuario a actualizar no existe.")))
                .flatMap(existingUser -> {
                    //se envia otro objeto modificado para que no pas por la restriccion
                    existingUser.setFirstName(user.getFirstName());
                    existingUser.setLastName(user.getLastName());
                    existingUser.setAddress(user.getAddress());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setBaseSalary(user.getBaseSalary());
                    existingUser.setBirthDate(user.getBirthDate());
                    existingUser.setIdentityDocument(user.getIdentityDocument());

                    return userRepository.save(existingUser);
                });
    }

    public Mono<Boolean> deleteById(UUID id) {
        return userRepository.deleteById(id);
    }

    public Mono<Boolean> checkUserExistsByDocument(String identityDocument) {
        return userRepository.existsByIdentityDocument(identityDocument);
    }

    public Mono<Boolean> checkUserExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private Mono<User> validateUser(User user) {
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            return Mono.error(new IllegalArgumentException("El campo First Name es obligatorio."));
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            return Mono.error(new IllegalArgumentException("El campo Last Name es obligatorio."));
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return Mono.error(new IllegalArgumentException("El campo Email es obligatorio."));
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return Mono.error(new IllegalArgumentException("El formato del correo electrónico es inválido."));
        }
        if (user.getBaseSalary() == null) {
            return Mono.error(new IllegalArgumentException("El campo Base Salary es obligatorio."));
        }
        if (user.getBaseSalary().compareTo(new BigDecimal("0")) <= 0 || user.getBaseSalary().compareTo(new BigDecimal("1500000")) > 0) {
            return Mono.error(new IllegalArgumentException("El salario base debe ser un valor numérico entre 0 y 1.500.000."));
        }
        return Mono.just(user);
    }
}
