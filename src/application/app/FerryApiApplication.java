package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"controllers", "service", "port", "adapter", "database", "config"})
public class FerryApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(FerryApiApplication.class, args);
    }
}

