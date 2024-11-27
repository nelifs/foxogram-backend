package su.foxogram.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("api")
@Getter
@Setter
public class APIConfig {
    private String version;
    private String env;

    @Bean
    public boolean isDevelopment() {
        return env.equals("dev");
    }
}
