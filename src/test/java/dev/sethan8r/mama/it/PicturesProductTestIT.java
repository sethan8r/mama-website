package dev.sethan8r.mama.it;

import dev.sethan8r.mama.dto.PicturesProductResponseAdminDto;
import dev.sethan8r.mama.dto.UploadPicturesResponseDto;
import dev.sethan8r.mama.model.Category;
import dev.sethan8r.mama.model.PicturesProduct;
import dev.sethan8r.mama.model.Product;
import dev.sethan8r.mama.repository.CategoryRepository;
import dev.sethan8r.mama.repository.PicturesProductRepository;
import dev.sethan8r.mama.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PicturesProductTestIT extends BaseIntegrationTest {

    @Autowired
    private PicturesProductRepository picturesProductRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private PicturesProduct testPicture;
    private Product testProduct;

    @BeforeEach
    public void setup() {
        picturesProductRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        var bytes = "test image content".getBytes();
        var category = categoryRepository.save(new Category(null, "TestCategory"));

        var product = new Product();
        product.setName("product");
        product.setDescription("description");
        product.setSlug("test-product-setup");
        product.setCategory(category);
        product.setAvailable(true);
        product.setDeleted(false);
        testProduct = productRepository.save(product);

        var picturesProduct = new PicturesProduct();
        picturesProduct.setImage(bytes);
        picturesProduct.setProduct(testProduct);
        picturesProduct.setSortOrder(0);

        testPicture = picturesProductRepository.save(picturesProduct);
    }

    @Test
    void createPicturesProductManyTest() {
        var category = categoryRepository.save(new Category(null, "TestCategory2"));

        var product = new Product();
        product.setName("TestProduct");
        product.setDescription("Description");
        product.setSlug("test-product");
        product.setCategory(category);
        product.setAvailable(true);
        product.setDeleted(false);
        var savedProduct = productRepository.save(product);

        byte[] fileContent1 = "fake image 1".getBytes();
        byte[] fileContent2 = "fake image 2".getBytes();

        var file1 = new ByteArrayResource(fileContent1) {
            @Override
            public String getFilename() {
                return "test-image-1.jpg";
            }
        };

        var file2 = new ByteArrayResource(fileContent2) {
            @Override
            public String getFilename() {
                return "test-image-2.jpg";
            }
        };

        var body = new LinkedMultiValueMap<String, Object>();
        body.add("files", file1);
        body.add("files", file2);
        body.add("sortOrders", new Integer[]{0, 1});

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var response = restTemplate.postForEntity(
                "/api/pictures/product/admin/" + savedProduct.getId() + "/more",
                new HttpEntity<>(body, headers),
                UploadPicturesResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().uploaded()).isEqualTo(2);
        assertThat(response.getBody().total()).isEqualTo(2);
    }

    @Test
    void updateSortOrderTest() {
        var url = UriComponentsBuilder.fromPath("/api/pictures/product/admin/{pictureId}")
                .queryParam("sortOrder", 5)
                .buildAndExpand(testPicture.getId())
                .toUriString();

        var response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                null,
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var res = picturesProductRepository.findById(testPicture.getId()).orElse(null);

        assertThat(res).isNotNull();
        assertThat(res.getSortOrder()).isEqualTo(5);
    }

    @Test
    void getPicturesProductByProductIdForAdminTest() {
        var response = restTemplate.exchange(
                "/api/pictures/product/admin/" + testProduct.getId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PicturesProductResponseAdminDto>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var res = response.getBody();

        assertThat(res).isNotNull().isNotEmpty();
        assertThat(res).hasSize(1);
        assertThat(res.getFirst().sortOrder()).isEqualTo(0);
    }

    @Test
    void deletePicturesProductByIdTest() {
        var response = restTemplate.exchange(
                "/api/pictures/product/admin/" + testPicture.getId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var res = picturesProductRepository.findById(testPicture.getId()).orElse(null);

        assertThat(res).isNull();
    }

    @Test
    void getPictureByIdTest() {
        var response = restTemplate.exchange(
                "/api/pictures/product/" + testPicture.getId() + "/image",
                HttpMethod.GET,
                null,
                byte[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var res = response.getBody();
        assertThat(res).isNotNull().isNotEmpty().isEqualTo("test image content".getBytes());
    }
}
