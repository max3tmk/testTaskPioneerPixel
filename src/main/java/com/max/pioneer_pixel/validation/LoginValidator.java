package com.max.pioneer_pixel.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class LoginValidator implements ConstraintValidator<ValidLogin, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{11,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;

        return PHONE_PATTERN.matcher(value).matches() || EMAIL_PATTERN.matcher(value).matches();
    }
}
