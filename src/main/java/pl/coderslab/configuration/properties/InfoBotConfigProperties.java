package pl.coderslab.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram.token.info")
@Data
public class InfoBotConfigProperties {
    private String name;
    private String token;
}
