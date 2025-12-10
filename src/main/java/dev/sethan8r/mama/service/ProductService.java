package dev.sethan8r.mama.service;

import dev.sethan8r.mama.dto.ProductRequestDto;
import dev.sethan8r.mama.dto.ProductResponseAdminDto;
import dev.sethan8r.mama.dto.ProductResponseDto;
import dev.sethan8r.mama.dto.ProductResponseShortDto;
import dev.sethan8r.mama.exception.AlreadyExistException;
import dev.sethan8r.mama.exception.NotFoundException;
import dev.sethan8r.mama.mapper.ProductMapper;
import dev.sethan8r.mama.model.Category;
import dev.sethan8r.mama.model.PicturesProduct;
import dev.sethan8r.mama.model.Product;
import dev.sethan8r.mama.repository.CategoryRepository;
import dev.sethan8r.mama.repository.PicturesProductRepository;
import dev.sethan8r.mama.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final PicturesProductRepository picturesProductRepository;

    @Transactional
    public ProductResponseAdminDto createProduct(ProductRequestDto dto) {
        log.info("Создание продукта с name={}", dto.name());

        if(productRepository.existsBySlug(dto.slug())) {
            throw new AlreadyExistException("\"" + dto.slug() + "\" уже существует");
        }

        Category category = categoryRepository.findByName(dto.categoryName()).orElseThrow(()
                -> new NotFoundException("Категории с названием \"" + dto.categoryName() + "\" не существует"));

        Product product = productMapper.toProduct(dto, category);
        productRepository.save(product);

        return productMapper.toProductResponseAdminDto(product);
    }

    @Transactional
    public void updateProductPrice(Long productId, String price) {
        log.info("Обновление цены продукта с id={}", productId);

        Product product = productRepository.findById(productId).orElseThrow(()
                -> new NotFoundException("Продукта с ID \"" + productId + "\" не существует"));
        product.setPrice(price);

        productRepository.save(product);
    }

    public Page<ProductResponseShortDto> getAllProductsShort(Pageable pageable) {
        log.info("Отображение всех продуктов (short)");

        Page<Product> products = productRepository.findByDeletedFalse(pageable);

        return mapToShortDtoWithMainPicture(products);
    }

    public ProductResponseAdminDto getProductBySlugForAdmin(String slug) {
        log.info("Отображение продукта для админа со slug={}", slug);

        Product product = productRepository.findBySlug(slug).orElseThrow(()
                -> new NotFoundException("Продукта с slug \"" + slug + "\" не существует"));

        return  productMapper.toProductResponseAdminDto(product);
    }

    public ProductResponseDto getProductBySlug(String slug) {
        log.info("Отображение продукта со slug={}", slug);

        Product product = productRepository.findBySlug(slug).orElseThrow(()
                -> new NotFoundException("Продукта с slug \"" + slug + "\" не существует"));

        return  productMapper.toProductResponseDto(product);
    }

    public Page<ProductResponseShortDto> getProductsByCategoryName(String categoryName, Pageable pageable) {
        log.info("Отображение продуктов в категории {}", categoryName);

        Page<Product> products = productRepository.findByCategoryNameAndIsDeletedFalse(categoryName, pageable);

        return mapToShortDtoWithMainPicture(products);
    }

    public Page<ProductResponseShortDto> searchProductsByName(String name, Pageable pageable) {
        log.info("Поиск продуктов с name={}", name);

        Page<Product> products = productRepository.findByNameContainingIgnoreCaseAndDeletedFalse(name, pageable);

        return mapToShortDtoWithMainPicture(products);
    }

    public Page<ProductResponseShortDto> searchProductsByNameForAdmin(String name, Pageable pageable) {
        log.info("Поиск продуктов для админа с name={}", name);

        Page<Product> products = productRepository.findByNameContainingIgnoreCase(name, pageable);

        return mapToShortDtoWithMainPicture(products);
    }

    @Transactional
    public void updateProductIsAvailable(Long productId) {
        log.info("Изменение активности продукта с id={}", productId);

        Product product = productRepository.findById(productId).orElseThrow(()
                -> new NotFoundException("Продукта с ID \"" + productId + "\" не существует"));


        product.setAvailable(!product.isAvailable());
        productRepository.save(product);
    }

    @Transactional
    public void updateProductIsDeleted(Long productId) {
        log.info("Изменение отображения продукта с id={}", productId);

        Product product = productRepository.findById(productId).orElseThrow(()
                -> new NotFoundException("Продукта с ID \"" + productId + "\" не существует"));

        product.setDeleted(!product.isDeleted());
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        log.info("Полное удаление продукта с id={}", productId);

        productRepository.deleteById(productId);
    }

    private Page<ProductResponseShortDto> mapToShortDtoWithMainPicture(Page<Product> products) {

        List<Long> ids = products.stream()
                .map(Product::getId)
                .toList();

        List<PicturesProduct> pics = picturesProductRepository
                .findByProductIdInAndSortOrder(ids, 0);

        Map<Long, PicturesProduct> mainPicMap = pics.stream()
                .collect(Collectors.toMap(
                        p -> p.getProduct().getId(),
                        p -> p
                ));

        return products.map(product -> {
            PicturesProduct mainPic = mainPicMap.get(product.getId());
            return productMapper.toProductResponseShortDto(product, mainPic);
        });
    }
}
