package dev.sethan8r.mama.dto;

import java.util.List;
import java.util.Map;

public record ProductResponseDto(
        String name,
        String description,
        String price,
        Map<String, String> characteristics,
        String categoryName,
        List<PicturesProductResponseDto> pictures,
        boolean available,
        boolean deleted
) { }
