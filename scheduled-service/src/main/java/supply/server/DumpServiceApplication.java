package supply.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DumpServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DumpServiceApplication.class, args);
	}

}
