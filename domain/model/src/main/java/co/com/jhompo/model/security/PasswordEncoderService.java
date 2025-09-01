package co.com.jhompo.model.security;

import reactor.core.publisher.Mono;

public interface PasswordEncoderService {
    Mono<String> encode(CharSequence rawPassword);
    Mono<Boolean> matches(CharSequence rawPassword, String encodedPassword);
}
