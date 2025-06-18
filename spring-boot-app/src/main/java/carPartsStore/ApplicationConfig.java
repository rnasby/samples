package carPartsStore;

import carPartsStore.auth.AuthConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;

@Configuration
@Import({AuthConfig.class, ApplicationTraits.class})
public class ApplicationConfig {
    private final DataSource dataSource;

    ApplicationConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}