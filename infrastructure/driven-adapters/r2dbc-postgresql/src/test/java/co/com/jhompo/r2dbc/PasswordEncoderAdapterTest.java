package co.com.jhompo.r2dbc;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderAdapterTest {

    @Test
    void testEncodeAndMatches() {
        PasswordEncoderAdapter adapter = new PasswordEncoderAdapter();

        String rawPassword = "mySecret123";

        // Encode
        String encoded = adapter.encode(rawPassword).block();
        assertThat(encoded).isNotNull();
        assertThat(encoded).isNotEqualTo(rawPassword);

        // Matches
        Boolean matches = adapter.matches(rawPassword, encoded).block();
        assertThat(matches).isTrue();

        // Matches con contraseña incorrecta
        Boolean wrongMatches = adapter.matches("wrongPassword", encoded).block();
        assertThat(wrongMatches).isFalse();
    }
}
