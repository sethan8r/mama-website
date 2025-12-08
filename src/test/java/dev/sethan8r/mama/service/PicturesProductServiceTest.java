package dev.sethan8r.mama.service;

import dev.sethan8r.mama.dto.PicturesProductResponseAdminDto;
import dev.sethan8r.mama.dto.PicturesProductResponseDto;
import dev.sethan8r.mama.exception.InvalidRequestException;
import dev.sethan8r.mama.exception.NotFoundException;
import dev.sethan8r.mama.mapper.PicturesProductMapper;
import dev.sethan8r.mama.model.PicturesProduct;
import dev.sethan8r.mama.model.Product;
import dev.sethan8r.mama.repository.PicturesProductRepository;
import dev.sethan8r.mama.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PicturesProductServiceTest {

    @Mock
    private PicturesProductRepository picturesProductRepository;

    @Mock
    private PicturesProductMapper picturesProductMapper;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PicturesProductService picturesProductService;

    private PicturesProduct  picturesProduct;
    private Product product;
    private PicturesProductResponseAdminDto picturesProductResponseAdminDto;
    private PicturesProductResponseDto picturesProductResponseDto;
    private byte[] testByte;

    @BeforeEach
    public void setup() {
        testByte = new byte[]{1,2,3,4,5};

        product = new Product();
        product.setId(1L);
        product.setName("product");
        product.setDescription("description");

        picturesProduct = new PicturesProduct();
        picturesProduct.setId(1L);
        picturesProduct.setImage(testByte);
        picturesProduct.setProduct(product);
        picturesProduct.setSortOrder(1);

        picturesProductResponseAdminDto = new PicturesProductResponseAdminDto(
                1L, "/api/pictures/product/1/image", 1L, "product", 1);

        picturesProductResponseDto = new PicturesProductResponseDto("/api/pictures/product/1/image");
    }

    @Test
    void createPicturesProductManyTest() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                testByte
        );

        MultipartFile[] files = new MultipartFile[]{file, file};

        Integer[] sortOrders =  new Integer[]{0,1};

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(picturesProductRepository.saveAll(any())).thenReturn(any());

        ArgumentCaptor<List<PicturesProduct>> captor = ArgumentCaptor.forClass(List.class);

        var res = picturesProductService.createPicturesProductMany(1L, sortOrders, files);

        verify(productRepository).findById(1L);
        verify(picturesProductRepository).saveAll(captor.capture());

        List<PicturesProduct> savedList = captor.getValue();
        assertThat(res.total()).isEqualTo(2);
        assertThat(res.uploaded()).isEqualTo(2);
        assertThat(savedList).hasSize(2);
        assertThat(savedList.get(0).getSortOrder()).isEqualTo(0);
        assertThat(savedList.get(1).getSortOrder()).isEqualTo(1);
    }

    @Test
    void createPicturesProductManyThrowLengthTest() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                testByte
        );

        MultipartFile[] files = new MultipartFile[]{file, file};
        Integer[] sortOrders =  new Integer[]{0};

        assertThatThrownBy(() -> picturesProductService
                .createPicturesProductMany(1L, sortOrders, files))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void createPicturesProductManyThrowTest() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                testByte
        );

        MultipartFile[] files = new MultipartFile[]{file, file};

        Integer[] sortOrders =  new Integer[]{0,1};

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> picturesProductService
                .createPicturesProductMany(1L, sortOrders, files))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowWhenFileReadFails() throws IOException {
        Long productId = 1L;
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        MultipartFile badFile = mock(MultipartFile.class);
        when(badFile.isEmpty()).thenReturn(false);
        when(badFile.getBytes()).thenThrow(new IOException("Ошибка чтения"));
        when(badFile.getOriginalFilename()).thenReturn("broken.jpg");

        MultipartFile[] files = {badFile};
        Integer[] sortOrders = {1};

        assertThatThrownBy(() -> picturesProductService
                .createPicturesProductMany(1L, sortOrders, files))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void createPicturesProductTest() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                testByte
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(picturesProductRepository.save(any(PicturesProduct.class)))
                .thenAnswer(invocation -> { //имитируем работу БД
                    PicturesProduct input = invocation.getArgument(0);
                    input.setId(123L); // добавляем ID, как делает БД
                    return input;
        }); //-- оно мне не надо

        ArgumentCaptor<PicturesProduct> captor = ArgumentCaptor.forClass(PicturesProduct.class);

        picturesProductService.createPicturesProduct(1L,0, file);

        verify(productRepository).findById(1L);
        verify(picturesProductRepository).save(captor.capture());

        var res = captor.getValue();

        assertThat(res.getProduct().getId()).isEqualTo(1L);
        assertThat(res.getSortOrder()).isEqualTo(0);
        assertThat(res.getImage()).isEqualTo(testByte);
    }

    @Test
    void updateSortOrderTest() {
        when(picturesProductRepository.findById(1L)).thenReturn(Optional.of(picturesProduct));

        picturesProductService.updateSortOrder(1L, 2);

        ArgumentCaptor<PicturesProduct> captor = ArgumentCaptor.forClass(PicturesProduct.class);

        verify(picturesProductRepository).findById(1L);
        verify(picturesProductRepository).save(captor.capture());

        var res =  captor.getValue();

        assertThat(res.getSortOrder()).isEqualTo(2);
    }

    @Test
    void getPictureById() {
        when(picturesProductRepository.findById(1L)).thenReturn(Optional.of(picturesProduct));

        var res = picturesProductService.getPictureById(1L);

        verify(picturesProductRepository).findById(1L);

        assertThat(res).isEqualTo(picturesProduct.getImage());
    }
}
