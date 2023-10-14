package pl.coderslab.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram.token.var")
@Data
public class TokenConfigProperties {
    private String name;
    private String token;
}
