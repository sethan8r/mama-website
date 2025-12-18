package dev.sethan8r.mama.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record ProductRequestDto(
        @NotBlank(message = "Поле не должно быть пустым")
        @Size(max = 100)
        String name,
        @NotBlank(message = "Поле не должно быть пустым")
        @Size(max = 255)
        String description,
        String price,
        Map<String, String> characteristics,
        @NotBlank(message = "Поле не должно быть пустым")
        String categoryName,
        @NotNull(message = "Поле не должно быть пустым")
        boolean available,
        @NotNull(message = "Поле не должно быть пустым")
        boolean deleted,
        @NotBlank(message = "Поле не должно быть пустым")
        String slug
) { }
