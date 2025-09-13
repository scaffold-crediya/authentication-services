package co.com.jhompo.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CorsConfig.class)
class CorsConfigSpringTest {

    @Autowired
    private CorsWebFilter corsWebFilter;

    @Test
    void testCorsWebFilterBeanExists() {
        // Verifica que el bean se haya creado correctamente
        assertThat(corsWebFilter).isNotNull();
    }
}
