package in.ripple.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@ComponentScan(basePackages = "in.ripple.user")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
