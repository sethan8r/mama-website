package dev.sethan8r.mama.dto;

import java.util.List;
import java.util.Map;

public record ProductResponseAdminDto(
        Long id,
        String name,
        String description,
        String price,
        Map<String, String> characteristics,
        String categoryName,
        List<PicturesProductResponseAdminDto> picturesProduct,
        boolean available,
        boolean deleted,
        String slug
) { }
