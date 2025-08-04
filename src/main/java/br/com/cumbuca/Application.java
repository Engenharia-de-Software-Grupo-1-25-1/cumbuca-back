package br.com.cumbuca;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Objects;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        final Dotenv dotenv = Dotenv.configure().load();

        System.setProperty("DB_URL", Objects.requireNonNull(dotenv.get("DB_URL")));
        System.setProperty("DB_USER", Objects.requireNonNull(dotenv.get("DB_USER")));
        System.setProperty("DB_PASS", Objects.requireNonNull(dotenv.get("DB_PASS")));

        System.setProperty("JWT_SECRET", Objects.requireNonNull(dotenv.get("JWT_SECRET")));
        System.setProperty("JWT_EXPIRATION_MINUTES", Objects.requireNonNull(dotenv.get("JWT_EXPIRATION_MINUTES")));

        SpringApplication.run(Application.class, args);
    }
}
