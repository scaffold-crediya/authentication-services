package co.com.jhompo.usecase.user;

import co.com.jhompo.model.security.PasswordEncoderService;
import co.com.jhompo.model.user.User;
import co.com.jhompo.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static co.com.jhompo.util.Messages.*;


@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(USER.EMAIL_REGEX);
    private final PasswordEncoderService passwordEncoder;

    public Mono<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Flux<User> findAll() {
        return userRepository.findAll();
    }

    public Mono<User> createUser(User user) {

        return validateUser(user)
                .flatMap(validatedUser ->
                        userRepository.findByEmail(validatedUser.getEmail())
                                .hasElement()
                                .flatMap(emailExists -> {
                                    if (emailExists) {
                                        return Mono.error(new IllegalArgumentException(USER.EMAIL_ALREADY_EXISTS));
                                    }
                                    return userRepository.existsByIdentityDocument(validatedUser.getIdentityDocument())
                                            .flatMap(docExists -> {
                                                if (docExists) {
                                                    return Mono.error(new IllegalArgumentException(USER.DOCUMENT_EXISTS));
                                                }

                                                // aquí hacemos la encriptación de forma reactiva
                                                return passwordEncoder.encode(validatedUser.getPassword())
                                                        .flatMap(hashedPassword -> {
                                                            User userToSave = validatedUser.toBuilder()
                                                                    .password(hashedPassword)
                                                                    .build();

                                                            return userRepository.save(userToSave);
                                                        });
                                            });
                                }));
    }


    public Mono<User> updateUser(User user) {
        if (user.getId() == null) {
            return Mono.error(new IllegalArgumentException(USER.ID_REQUIRED));
        }
        return validateUser(user)
                .then(userRepository.findById(user.getId()))
                .switchIfEmpty(Mono.error(new RuntimeException(USER.NOT_FOUND)))
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

    public Mono<Void> deleteById(UUID id) {
        return userRepository.deleteById(id);
    }

    public Mono<Boolean> checkUserExistsByDocument(String identityDocument) {
        return userRepository.existsByIdentityDocument(identityDocument);
    }

    public Mono<User> checkUserExistsByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Flux<User> findDetailsByEmails(List<String> emails) {
        return userRepository.findByEmailIn(emails);
    }

    public Mono<User> validateUser(User user) {
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            return Mono.error(new IllegalArgumentException(USER.NAME_REQUIRED));
        }
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            return Mono.error(new IllegalArgumentException(USER.LASTNAME_REQUIRED));
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return Mono.error(new IllegalArgumentException(USER.EMAIL_REQUIRED));
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return Mono.error(new IllegalArgumentException(USER.EMAIL_INVALID));
        }
        if (user.getBaseSalary() == null) {
            return Mono.error(new IllegalArgumentException(USER.SALARY_BASE_REQUIRED));
        }
        if (user.getBaseSalary().compareTo(new BigDecimal("0")) <= 0 || user.getBaseSalary().compareTo(new BigDecimal("1500000")) > 0) {
            return Mono.error(new IllegalArgumentException(USER.SALARY_BASE_RULE));
        }
        return Mono.just(user);
    }


}
