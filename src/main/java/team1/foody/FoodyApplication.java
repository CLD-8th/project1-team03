package team1.foody;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FoodyApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodyApplication.class, args);
	}

}
