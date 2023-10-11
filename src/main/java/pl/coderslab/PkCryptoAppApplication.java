package pl.coderslab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.coderslab.configuration.TelegramConfiguration;
import pl.coderslab.configuration.TokenConfigProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(TokenConfigProperties.class)
public class PkCryptoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(PkCryptoAppApplication.class, args);
	}

}
