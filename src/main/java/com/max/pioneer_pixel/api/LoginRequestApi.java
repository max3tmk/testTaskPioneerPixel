package com.max.pioneer_pixel.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestApi {

    @Schema(description = "Email или телефон (только цифры)", example = "email1@email.com / 37520000001")
    @NotBlank(message = "Login must not be blank")
    private String login;

    @Schema(description = "Пароль (не менее 8 символов)", example = "password1")
    @NotBlank(message = "Password must not be blank")
    private String password;
}
