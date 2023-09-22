package pl.coderslab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PkCryptoAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(PkCryptoAppApplication.class, args);
	}

}
