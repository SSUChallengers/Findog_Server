package challengers.findog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FindogApplication {

	public static void main(String[] args) {
		SpringApplication.run(FindogApplication.class, args);
	}

}
