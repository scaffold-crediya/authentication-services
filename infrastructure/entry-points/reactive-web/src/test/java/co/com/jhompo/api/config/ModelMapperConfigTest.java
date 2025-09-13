package co.com.jhompo.api.config;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;

class ModelMapperConfigTest {

    @Test
    void testModelMapperBeanCreation() {
        ModelMapperConfig config = new ModelMapperConfig();
        ModelMapper modelMapper = config.modelMapper();

        assertThat(modelMapper).isNotNull();
        assertThat(modelMapper).isInstanceOf(ModelMapper.class);
    }
}
