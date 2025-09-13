package co.com.jhompo.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import static org.assertj.core.api.Assertions.assertThat;

class UseCasesConfigTest {

    @Test
    void shouldInstantiateUseCasesConfig() {
        // JaCoCo medirá esto como ejecución del constructor
        UseCasesConfig config = new UseCasesConfig();
        assertThat(config).isNotNull();
        assertThat(config.getClass()).isEqualTo(UseCasesConfig.class);
    }

    @Test
    void shouldHaveConfigurationAnnotation() {
        Configuration configAnnotation = AnnotationUtils.findAnnotation(UseCasesConfig.class, Configuration.class);
        assertThat(configAnnotation).isNotNull();
    }

    @Test
    void shouldHaveComponentScanAnnotation() {
        ComponentScan componentScanAnnotation = AnnotationUtils.findAnnotation(UseCasesConfig.class, ComponentScan.class);
        assertThat(componentScanAnnotation).isNotNull();
    }

    @Test
    void shouldHaveCorrectBasePackage() {
        ComponentScan componentScanAnnotation = AnnotationUtils.findAnnotation(UseCasesConfig.class, ComponentScan.class);
        String[] basePackages = componentScanAnnotation.basePackages();

        assertThat(basePackages).hasSize(1);
        assertThat(basePackages[0]).isEqualTo("co.com.jhompo.usecase");
    }

}