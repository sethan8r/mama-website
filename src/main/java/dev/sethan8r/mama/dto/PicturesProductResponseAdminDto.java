package dev.sethan8r.mama.dto;

public record PicturesProductResponseAdminDto(
        Long id,
        String url,
        Long productId,
        String productName,
        Integer sortOrder
) { }
