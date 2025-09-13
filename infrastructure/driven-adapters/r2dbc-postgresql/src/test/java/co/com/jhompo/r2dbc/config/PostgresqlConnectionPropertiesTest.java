package co.com.jhompo.r2dbc.config;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import co.com.jhompo.r2dbc.config.PostgresqlConnectionProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = PostgresqlConnectionPropertiesTest.TestConfig.class)
@TestPropertySource(properties = {
        "spring.r2dbc.host=localhost",
        "spring.r2dbc.port=5432",
        "spring.r2dbc.database=test_db",
        "spring.r2dbc.schema=public",
        "spring.r2dbc.username=test_user",
        "spring.r2dbc.password=test_password"
})
class PostgresqlConnectionPropertiesTest {

    @Autowired
    private PostgresqlConnectionProperties connectionProperties;

    @Test
    void shouldBindPropertiesCorrectly() {
        assertNotNull(connectionProperties, "El objeto de propiedades no debe ser nulo.");
        assertEquals("localhost", connectionProperties.host());
        assertEquals(5432, connectionProperties.port());
        assertEquals("test_db", connectionProperties.database());
        assertEquals("public", connectionProperties.schema());
        assertEquals("test_user", connectionProperties.username());
        assertEquals("test_password", connectionProperties.password());
    }

    @Configuration
    @EnableConfigurationProperties(PostgresqlConnectionProperties.class) // This is the key line
    static class TestConfig {
        // Now, Spring knows to create a bean from your PostgresqlConnectionProperties record
    }
}