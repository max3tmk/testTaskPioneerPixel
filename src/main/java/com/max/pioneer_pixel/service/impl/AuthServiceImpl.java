package com.max.pioneer_pixel.service.impl;

import com.max.pioneer_pixel.dao.UserDao;
import com.max.pioneer_pixel.model.User;
import com.max.pioneer_pixel.security.JwtUtil;
import com.max.pioneer_pixel.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public String loginByEmail(String email, String password) {
        User user = userDao.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return jwtUtil.generateToken(user.getId().toString());
    }

    @Override
    public String loginByPhone(String phone, String password) {
        User user = userDao.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("Invalid phone or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid phone or password");
        }

        return jwtUtil.generateToken(user.getId().toString());
    }
}
