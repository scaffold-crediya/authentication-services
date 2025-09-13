package co.com.jhompo.r2dbc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@jakarta.annotation.Generated(
        value = "configuration-properties",
        comments = "Simple configuration record - no business logic to test"
)
@ConfigurationProperties(prefix = "spring.r2dbc")
public record PostgresqlConnectionProperties(
        String host,
        Integer port,
        String database,
        String schema,
        String username,
        String password) {
}
