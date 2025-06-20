package com.max.pioneer_pixel.service;

public interface AuthService {

    String loginByEmail(String email, String password);

    String loginByPhone(String phone, String password);
}
