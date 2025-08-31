package co.com.jhompo.usecase.user;

import co.com.jhompo.model.user.User;
import co.com.jhompo.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    // En un proyecto real, inyectarías un PasswordEncoder aquí
    // private final PasswordEncoder passwordEncoder;

    public Mono<User> execute(String email, String password) {
        return userRepository.findByEmail(email)
               .switchIfEmpty(Mono.error(new RuntimeException("Authentication failed: User not found")))
                .flatMap(user -> {
                    if (password.equals(user.getPassword())) {
                        return Mono.just(user);
                    } else {
                        return Mono.error(new RuntimeException("Authentication failed: Invalid credentials"));
                    }
                });
    }
}