package su.foxogram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "*")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}