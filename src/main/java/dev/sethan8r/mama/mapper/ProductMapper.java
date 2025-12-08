package dev.sethan8r.mama.mapper;

import dev.sethan8r.mama.dto.ProductRequestDto;
import dev.sethan8r.mama.dto.ProductResponseAdminDto;
import dev.sethan8r.mama.dto.ProductResponseDto;
import dev.sethan8r.mama.dto.ProductResponseShortDto;
import dev.sethan8r.mama.model.Category;
import dev.sethan8r.mama.model.PicturesProduct;
import dev.sethan8r.mama.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PicturesProductMapper.class})
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "picturesProduct", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "name", source = "productRequestDto.name")
//    @Mapping(target = "isAvailable", source = "productRequestDto.isAvailable")
//    @Mapping(target = "isDeleted", source = "productRequestDto.isDeleted")
    Product toProduct(ProductRequestDto productRequestDto, Category category);

    @Mapping(target = "categoryName", expression = "java(product.getCategory().getName())")
    @Mapping(target = "pictures", source = "picturesProduct")
//    @Mapping(target = "isAvailable", source = "product.isAvailable")
//    @Mapping(target = "isDeleted", source = "product.isDeleted")
    ProductResponseDto  toProductResponseDto(Product product);

    @Mapping(target = "mainPictureUrl", expression = "java(getUrl(picturesProduct))")
//    @Mapping(target = "isAvailable", source = "product.isAvailable")
//    @Mapping(target = "isDeleted", source = "product.isDeleted")
    ProductResponseShortDto toProductResponseShortDto(Product product, PicturesProduct  picturesProduct);

    @Mapping(target = "categoryName", source = "category.name")
//    @Mapping(target = "isAvailable", source = "product.isAvailable")  // явно маппим boolean поля
//    @Mapping(target = "isDeleted", source = "product.isDeleted")
    ProductResponseAdminDto  toProductResponseAdminDto(Product product);

    default String getUrl(PicturesProduct picturesProduct) {
        if (picturesProduct == null) return null;
        return "/api/pictures/product/" + picturesProduct.getId() + "/image";
    }
}
