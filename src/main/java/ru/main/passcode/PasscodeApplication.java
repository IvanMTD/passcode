package ru.main.passcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PasscodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(PasscodeApplication.class, args);
    }
}
