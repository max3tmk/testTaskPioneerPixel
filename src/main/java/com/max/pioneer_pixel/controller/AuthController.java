package com.max.pioneer_pixel.controller;

import com.max.pioneer_pixel.api.LoginRequestApi;
import com.max.pioneer_pixel.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestApi loginRequest) {
        LocalDateTime now = LocalDateTime.now();
        String login = loginRequest.getLogin();
        log.info("[{}] Operation: login | Params: login={}", now, login);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, loginRequest.getPassword())
            );

            String token = jwtUtil.generateToken(login);

            log.info("[{}] Result: login successful | login={}", now, login);
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (AuthenticationException e) {
            log.warn("[{}] Error: invalid credentials for login={}", now, login);
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    private record AuthResponse(String token) {
    }
}
