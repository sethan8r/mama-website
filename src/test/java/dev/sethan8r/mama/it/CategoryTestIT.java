package dev.sethan8r.mama.it;

import dev.sethan8r.mama.dto.CategoryRequestDto;
import dev.sethan8r.mama.dto.CategoryResponseDto;
import dev.sethan8r.mama.model.Category;
import dev.sethan8r.mama.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryTestIT extends BaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setup() {
        categoryRepository.deleteAll();
        categoryRepository.save(new Category(null, "Test"));
    }

    @Test
    void createCategoryTest() {
        var dto = new CategoryRequestDto("Category");

        var res = restTemplate.postForEntity("/api/category/admin", dto,
                Void.class);

        assertThat(res).isNotNull();
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var category = categoryRepository.findByName("Category").orElse(null);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("Category");
    }

    @Test
    void updateCategoryNameTest() {
        var url = UriComponentsBuilder.fromPath("/api/category/admin/{categoryName}")
                .queryParam("newName", "NewName")
                .buildAndExpand("Test")
                .toUriString();

        var response = restTemplate.exchange(
                url,
                HttpMethod.PATCH,
                new HttpEntity<>(null),
                Void.class
        );

        var res = categoryRepository.findByName("NewName").orElse(null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(res).isNotNull();
        assertThat(res.getName()).isEqualTo("NewName");
    }

    @Test
    void getAllCategoriesTest() {
        var response = restTemplate.exchange(
                "/api/category",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CategoryResponseDto>>() {}
        );

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var res = response.getBody();

        assertThat(res).isNotNull().hasSize(1);
        assertThat(res.getFirst().name()).isEqualTo("Test");
    }

    @Test
    void deleteCategoryTest() {
        var response = restTemplate.exchange(
                "/api/category/admin/Test",
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var res = categoryRepository.findByName("Test").orElse(null);

        assertThat(res).isNull();
    }
}
