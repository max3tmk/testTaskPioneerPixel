package com.max.pioneer_pixel.service.jpa.impl;

import com.max.pioneer_pixel.dao.UserDao;
import com.max.pioneer_pixel.model.User;
import com.max.pioneer_pixel.service.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service("jpaUserSearchService")
@RequiredArgsConstructor
public class JpaUserSearchServiceImpl implements UserSearchService {

    private final UserDao userDao;

    @Override
    public Page<User> searchUsers(String name, String email, String phone, LocalDate dateOfBirth, Pageable pageable) {
        return userDao.searchUsers(name, email, phone, dateOfBirth, pageable);
    }
}
