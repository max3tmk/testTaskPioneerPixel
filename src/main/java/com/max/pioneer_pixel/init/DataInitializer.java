package com.max.pioneer_pixel.init;

import com.max.pioneer_pixel.util.UserDataGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserDataGenerator userDataGenerator;

    @Override
    public void run(String... args) {
        userDataGenerator.generateAndInsertUsers();
    }
}
