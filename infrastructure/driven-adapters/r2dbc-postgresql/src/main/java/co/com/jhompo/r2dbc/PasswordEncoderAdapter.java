package co.com.jhompo.r2dbc;

import co.com.jhompo.model.security.PasswordEncoderService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class PasswordEncoderAdapter implements PasswordEncoderService {

    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

    @Override
    public Mono<String> encode(CharSequence rawPassword) {
        return Mono.fromCallable(() -> delegate.encode(rawPassword))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> matches(CharSequence rawPassword, String encodedPassword) {
        return Mono.fromCallable(() -> delegate.matches(rawPassword, encodedPassword))
                .subscribeOn(Schedulers.boundedElastic());
    }
}