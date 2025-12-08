package dev.sethan8r.mama.mapper;

import dev.sethan8r.mama.dto.CategoryRequestDto;
import dev.sethan8r.mama.dto.CategoryResponseDto;
import dev.sethan8r.mama.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category toCategory(CategoryRequestDto dto);

    CategoryResponseDto toCategoryResponseDto(Category category);
}
