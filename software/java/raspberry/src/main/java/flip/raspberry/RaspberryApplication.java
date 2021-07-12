package flip.raspberry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@PropertySource("file:${user.home}/.flip/flip.properties")
@EnableScheduling
public class RaspberryApplication {

	public static void main(String[] args) {
		SpringApplication.run(RaspberryApplication.class, args)
			// Broadcasts  ContextStartedEvent:
			.start();
	}
}
