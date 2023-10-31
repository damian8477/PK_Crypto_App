package pl.coderslab.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "telegram.token.var")
@Component
@Data
public class TokenConfigProperties {
    private String name;
    private String token;
}
