package co.com.jhompo.usecase.user;

import co.com.jhompo.model.security.PasswordEncoderService;
import co.com.jhompo.model.user.User;
import co.com.jhompo.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderService passwordEncoder;

    public Mono<User> execute(String email, String password) {
        return userRepository.findByEmail(email)
               .switchIfEmpty(Mono.error(new IllegalArgumentException("Authentication failed: User not found")))
                .flatMap(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.just(user);
                    } else {
                        return Mono.error(new IllegalArgumentException("Authentication failed: Invalid credentials"));
                    }
                });
    }
}