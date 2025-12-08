package dev.sethan8r.mama.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeUsernameRequest(
        @NotBlank(message = "Поле не должно быть пустым")
        @Size(min = 5, message = "Username должен быть минимум 5 символов")
        String username
) { }
