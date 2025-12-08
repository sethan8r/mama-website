package dev.sethan8r.mama.mapper;

import dev.sethan8r.mama.dto.PicturesWorksRequestDto;
import dev.sethan8r.mama.dto.PicturesWorksResponseAdminDto;
import dev.sethan8r.mama.dto.PicturesWorksResponseDto;
import dev.sethan8r.mama.model.PicturesWorks;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PicturesWorksMapper {

    @Mapping(target = "url", expression = "java(getUrl(picturesWorks))")
    PicturesWorksResponseDto toPicturesWorksResponseDto(PicturesWorks picturesWorks);

    @Mapping(target = "url", expression = "java(getUrl(picturesWorks))")
    PicturesWorksResponseAdminDto toPicturesWorksResponseAdminDto(PicturesWorks picturesWorks);

    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "image",  source = "image")
    PicturesWorks toPicturesWorks(PicturesWorksRequestDto picturesWorksRequestDto, byte[] image);

    default String getUrl(PicturesWorks picturesWorks) {
        return "/api/pictures/works/" + picturesWorks.getId() + "/image";
    }
}
