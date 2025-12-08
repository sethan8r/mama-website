package dev.sethan8r.mama.dto;

public record ProductResponseShortDto(
        String name,
        String price,
        String mainPictureUrl,
        String slug,
        boolean available,
        boolean deleted
) { }
