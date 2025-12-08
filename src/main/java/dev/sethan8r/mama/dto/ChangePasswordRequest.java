package dev.sethan8r.mama.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Поле не должно быть пустым")
        String oldPassword,

        @NotBlank(message = "Поле не должно быть пустым")
        @Size(min = 8, message = "Пароль должен быть минимум 8 символов")
        String newPassword,

        @NotBlank(message = "Поле не должно быть пустым")
        String confirmPassword
) { }
