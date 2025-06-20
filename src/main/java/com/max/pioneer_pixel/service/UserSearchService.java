package com.max.pioneer_pixel.service;

import com.max.pioneer_pixel.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserSearchService {
    Page<User> searchUsers(String name, String email, String phone, LocalDate dateOfBirth, Pageable pageable);
}
