package com.max.pioneer_pixel.util;

import com.github.javafaker.Faker;
import com.max.pioneer_pixel.model.User;
import com.max.pioneer_pixel.service.EmailDataService;
import com.max.pioneer_pixel.service.PhoneDataService;
import com.max.pioneer_pixel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class UserDataGenerator {

    private final Faker faker = new Faker();
    private final Random random = new Random();

    private final UserService userService;
    private final EmailDataService emailDataService;
    private final PhoneDataService phoneDataService;

    @Value("${app.user-generator.count:10}")
    private int userCount;

    public void generateAndInsertUsers() {
        for (int i = 1; i <= userCount; i++) {
            String name = faker.name().firstName(); // только имя
            String email = "email" + i + "@email.com"; // email_i@email.com
            String phone = generatePhone(i); // формат 3752 + id + нули до 7 цифр
            String password = "password" + i; // password_i
            LocalDate dob = generateDateOfBirth();
            BigDecimal balance = generateInitialBalance();

            User user = new User();
            user.setName(name);
            user.setPassword(password);
            user.setDateOfBirth(dob);

            user = userService.createUserWithAccount(user, balance); // сохранили пользователя с аккаунтом
            emailDataService.addEmailData(user.getId(), email);
            phoneDataService.addPhoneData(user.getId(), phone);
        }
    }

    private String generatePhone(int userId) {
        // формируем строку с ведущими нулями до длины 7
        String paddedId = String.format("%07d", userId);
        return "3752" + paddedId;
    }

    private LocalDate generateDateOfBirth() {
        return LocalDate.now().minusYears(18 + random.nextInt(43)); // 18-60 лет
    }

    private BigDecimal generateInitialBalance() {
        return BigDecimal.valueOf(50 + random.nextInt(951)); // 50-1000
    }
}
