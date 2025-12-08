package dev.sethan8r.mama.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record ProductRequestDto(
        @NotBlank(message = "Поле не должно быть пустым")
        String name,
        @NotBlank(message = "Поле не должно быть пустым")
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
