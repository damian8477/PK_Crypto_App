package pl.coderslab.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram.token.vip")
@Data
public class VipSignalConfigProperties {
    private String name;
    private String token;
}
