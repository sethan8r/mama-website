package dev.sethan8r.mama.mapper;

import dev.sethan8r.mama.dto.PicturesProductResponseAdminDto;
import dev.sethan8r.mama.dto.PicturesProductResponseDto;
import dev.sethan8r.mama.model.PicturesProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PicturesProductMapper {

    @Mapping(target = "url", expression = "java(getUrl(picturesProduct))")
    PicturesProductResponseDto toPicturesProductDto(PicturesProduct picturesProduct);

    @Mapping(target = "url", expression = "java(getUrl(picturesProduct))")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    PicturesProductResponseAdminDto toPicturesProductResponseAdminDto(PicturesProduct picturesProduct);

    default String getUrl(PicturesProduct picturesProduct) {
        return "/api/pictures/product/" + picturesProduct.getId() + "/image";
    }
}
