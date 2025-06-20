package com.max.pioneer_pixel.dao;

import com.max.pioneer_pixel.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface UserDaoCustom {
    Page<User> searchUsers(String namePrefix, String email, String phone, LocalDate dateOfBirthAfter, Pageable pageable);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);
}
