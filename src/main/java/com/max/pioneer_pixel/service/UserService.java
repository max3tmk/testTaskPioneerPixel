package com.max.pioneer_pixel.service;

import com.max.pioneer_pixel.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    User createUserWithAccount(User user, java.math.BigDecimal initialBalance);

    Page<User> searchUsers(String name, String email, String phone, LocalDate dateOfBirth, Pageable pageable);

    // Добавление email
    void addEmail(Long userId, String email);

    // Удаление email
    void deleteEmail(Long userId, String email);

    // Добавление телефона
    void addPhone(Long userId, String phone);

    // Удаление телефона
    void deletePhone(Long userId, String phone);

    // Получить Email-адреса пользователя
    List<String> getEmails(Long userId);

    // Получить телефоны пользователя
    List<String> getPhones(Long userId);
}
