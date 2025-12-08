package dev.sethan8r.mama.service;

import dev.sethan8r.mama.dto.*;
import dev.sethan8r.mama.exception.AlreadyExistException;
import dev.sethan8r.mama.exception.NotFoundException;
import dev.sethan8r.mama.mapper.ProductMapper;
import dev.sethan8r.mama.model.Category;
import dev.sethan8r.mama.model.PicturesProduct;
import dev.sethan8r.mama.model.Product;
import dev.sethan8r.mama.repository.CategoryRepository;
import dev.sethan8r.mama.repository.PicturesProductRepository;
import dev.sethan8r.mama.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PicturesProductRepository picturesProductRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;
    private PicturesProduct picturesProduct;
    private ProductRequestDto productRequestDto;
    private ProductResponseAdminDto productResponseAdminDto;
    private ProductResponseDto productResponseDto;
    private ProductResponseShortDto productResponseShortDto;
    private Pageable pageable;
    private PicturesProductResponseAdminDto picturesProductResponseAdminDto;

    @BeforeEach
    public void setUp() {
        picturesProductResponseAdminDto = new PicturesProductResponseAdminDto(
                1L, "/api/pictures/product/1/image", 1L, "Product", 1);

        product = new Product();

        category = new Category();
        category.setId(1L);
        category.setName("Category");

        picturesProduct = new  PicturesProduct();
        picturesProduct.setId(1L);
        picturesProduct.setImage(new  byte[]{1,2,3,4,5});
        picturesProduct.setProduct(product);
        picturesProduct.setSortOrder(0);

        product.setId(1L);
        product.setName("Product");
        product.setDescription("Description");
        product.setPrice("1000");
        product.setCharacteristics(Map.of(
                "Материал", "Полиэстер",
                "Утеплитель", "Синтепон",
                "Цвет", "Белый"
        ));
        product.setCategory(category);
        product.setPicturesProduct(List.of(picturesProduct, picturesProduct));
        product.setAvailable(true);
        product.setDeleted(false);
        product.setSlug("kurtka-white");

        productRequestDto = new ProductRequestDto(
                "Product",
                "Description",
                "1000",
                new HashMap<>(Map.of(
                        "Материал", "Полиэстер",
                        "Утеплитель", "Синтепон",
                        "Цвет", "Белый"
                )),
                "Category",
                true,
                false,
                "kurtka-white"
        );

        productResponseAdminDto = new ProductResponseAdminDto(
                1L,
                "Product",
                "Description",
                "1000",
                new HashMap<>(Map.of(
                        "Материал", "Полиэстер",
                        "Утеплитель", "Синтепон",
                        "Цвет", "Белый"
                )),
                "Category",
                new ArrayList<>(List.of(
                        picturesProductResponseAdminDto,
                        picturesProductResponseAdminDto
                )),
                true,
                false,
                "kurtka-white"
        );

        productResponseDto = new ProductResponseDto(
                "Product",
                "Description",
                "1000",
                new HashMap<>(Map.of(
                        "Материал", "Полиэстер",
                        "Утеплитель", "Синтепон",
                        "Цвет", "Белый"
                )),
                "Category",
                new ArrayList<>(List.of(
                        new PicturesProductResponseDto("/api/pictures/product/1/image")
                )),
                true,
                false
        );

        productResponseShortDto = new ProductResponseShortDto(
                "Product",
                "1000",
                "/api/pictures/product/1/image",
                "kurtka-white",
                true,
                false
        );

        pageable = PageRequest.of(0, 2);
    }

    @Test
    void createProductTest() {
        when(productRepository.existsBySlug("kurtka-white")).thenReturn(false);
        when(categoryRepository.findByName("Category")).thenReturn(Optional.of(category));
        when(productMapper.toProduct(productRequestDto, category)).thenReturn(product);
        when(productMapper.toProductResponseAdminDto(product)).thenReturn(productResponseAdminDto);

        var res = productService.createProduct(productRequestDto);

        verify(productRepository).existsBySlug("kurtka-white");
        verify(categoryRepository).findByName("Category");
        verify(productMapper).toProduct(productRequestDto, category);
        verify(productMapper).toProductResponseAdminDto(product);

        assertThat(res).isNotNull();
        assertThat(res.name()).isEqualTo("Product");
    }

    @Test
    void createProductThrowTest() {
        when(productRepository.existsBySlug("kurtka-white")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(productRequestDto))
                .isInstanceOf(AlreadyExistException.class);
    }

    @Test
    void updateProductPrice() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.updateProductPrice(1L, "от 5000");

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        verify(productRepository).findById(1L);
        verify(productRepository).save(captor.capture());

        Product res = captor.getValue();

        assertThat(res).isNotNull();
        assertThat(res.getPrice()).isEqualTo("от 5000");
    }

    @Test
    void getAllProductsShortTest() {
        Page<Product> pages = new PageImpl<>(List.of(product, product));

        when(productRepository.findByDeletedFalse(pageable)).thenReturn(pages);
        when(picturesProductRepository.findByProductIdInAndSortOrder(List.of(1L, 1L), 0))
                .thenReturn(List.of(picturesProduct));
        when(productMapper.toProductResponseShortDto(product, picturesProduct))
                .thenReturn(productResponseShortDto);

        var result = productService.getAllProductsShort(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(productRepository).findByDeletedFalse(pageable);
        verify(picturesProductRepository).findByProductIdInAndSortOrder(List.of(1L, 1L), 0);
        verify(productMapper, times(2)).toProductResponseShortDto(product, picturesProduct);
    }

    @Test
    void getProductBySlugForAdminTest() {
        when(productRepository.findBySlug("kurtka-white")).thenReturn(Optional.of(product));
        when(productMapper.toProductResponseAdminDto(product)).thenReturn(productResponseAdminDto);

        var result = productService.getProductBySlugForAdmin("kurtka-white");

        verify(productRepository).findBySlug("kurtka-white");
        verify(productMapper).toProductResponseAdminDto(product);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Product");
    }

    @Test
    void getProductBySlugForAdminThrowTest() {
        when(productRepository.findBySlug("kurtka-white")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductBySlugForAdmin("kurtka-white"))
                .isInstanceOf(NotFoundException.class);

        verify(productRepository).findBySlug("kurtka-white");
    }

    @Test
    void getProductsByCategoryNameTest() {
        Page<Product> pages = new PageImpl<>(List.of(product, product));

        when(productRepository.findByCategoryNameAndIsDeletedFalse("Category", pageable))
                .thenReturn(pages);
        when(picturesProductRepository.findByProductIdInAndSortOrder(List.of(1L, 1L), 0))
                .thenReturn(List.of(picturesProduct));
        when(productMapper.toProductResponseShortDto(product, picturesProduct)).thenReturn(productResponseShortDto);

        var res =  productService.getProductsByCategoryName("Category", pageable);

        verify(productRepository).findByCategoryNameAndIsDeletedFalse("Category", pageable);
        verify(picturesProductRepository).findByProductIdInAndSortOrder(List.of(1L, 1L), 0);
        verify(productMapper, times(2)).toProductResponseShortDto(product, picturesProduct);

        assertThat(res).isNotNull();
        assertThat(res.getTotalElements()).isEqualTo(2);
    }

    @Test
    void updateProductIsAvailableTest() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        productService.updateProductIsAvailable(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).save(captor.capture());

        var res = captor.getValue();

        assertThat(res).isNotNull();
        assertThat(res.isAvailable()).isFalse();
        assertThat(res.isDeleted()).isFalse();
    }

    @Test
    void updateProductIsDeletedTest() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        productService.updateProductIsDeleted(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).save(captor.capture());

        var res = captor.getValue();

        assertThat(res).isNotNull();
        assertThat(res.isAvailable()).isTrue();
        assertThat(res.isDeleted()).isTrue();
    }
}
