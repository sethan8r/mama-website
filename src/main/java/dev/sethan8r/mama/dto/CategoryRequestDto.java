package dev.sethan8r.mama.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(
        @NotBlank(message = "Поле не должно быть пустым")
        String name
) { }
