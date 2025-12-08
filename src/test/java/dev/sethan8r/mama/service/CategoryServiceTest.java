package dev.sethan8r.mama.service;

import dev.sethan8r.mama.dto.CategoryRequestDto;
import dev.sethan8r.mama.dto.CategoryResponseDto;
import dev.sethan8r.mama.exception.AlreadyExistException;
import dev.sethan8r.mama.exception.EntityInUseException;
import dev.sethan8r.mama.mapper.CategoryMapper;
import dev.sethan8r.mama.model.Category;
import dev.sethan8r.mama.repository.CategoryRepository;
import dev.sethan8r.mama.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final CategoryMapper categoryMapper = mock(CategoryMapper.class);
    private final ProductRepository productRepository =  mock(ProductRepository.class);
    private final CategoryService categoryService = new CategoryService(categoryRepository, categoryMapper,
            productRepository);

    Category category;
    CategoryResponseDto  categoryResponseDto;
    CategoryRequestDto  categoryRequestDto;

    @BeforeEach
    public void setup() {
        category = new Category();
        category.setId(1L);
        category.setName("Category");

        categoryResponseDto = new CategoryResponseDto("Category");

        categoryRequestDto = new CategoryRequestDto("Category");
    }

    @Test
    void createCategoryTest() {
        when(categoryRepository.existsByName("Category")).thenReturn(false);
        when(categoryMapper.toCategory(categoryRequestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);

        categoryService.createCategory(categoryRequestDto);

        verify(categoryRepository, times(1)).existsByName("Category");
        verify(categoryMapper, times(1)).toCategory(categoryRequestDto);
        verify( categoryRepository, times(1)).save(category);
    }

    @Test
    void createCategoryThrowTest() {
        when(categoryRepository.existsByName("Category")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.createCategory(categoryRequestDto))
                .isInstanceOf(AlreadyExistException.class);

        verify(categoryRepository, never()).save(any());
        verify(categoryMapper, never()).toCategory(any());
    }

    @Test
    void updateCategoryTest() {
        when(categoryRepository.existsByName("Category")).thenReturn(false);
        when(categoryRepository.findByName("Category")).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        //any(Category.class) - любой класс

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class); //создание перехватчика

        categoryService.updateCategoryName("Category", "NewName");

        verify(categoryRepository).save(categoryCaptor.capture()); //делает сам перехват
        Category capturedCategory = categoryCaptor.getValue();

        assertThat("NewName").isEqualTo(capturedCategory.getName());
    }

    @Test
    void updateCategoryThrowTest() {
        when(categoryRepository.existsByName("NewName")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.updateCategoryName("Category",
                "NewName"))
                .isInstanceOf(AlreadyExistException.class);

        verify(categoryRepository, never()).save(any());
        verify(categoryRepository, never()).findByName(any());
    }

    @Test
    void getAllCategoriesTest() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toCategoryResponseDto(category)).thenReturn(categoryResponseDto);

        var list = categoryService.getAllCategories();

        verify(categoryRepository, times(1)).findAll();
        verify(categoryMapper, times(1)).toCategoryResponseDto(category);

        assertThat(list.getFirst()).isEqualTo(categoryResponseDto);
    }

    @Test
    public void deleteCategory() {
        when(categoryRepository.findByName("Category")).thenReturn(Optional.of(category));
        when(productRepository.existsByCategory(any())).thenReturn(false);
        doNothing().when(categoryRepository).delete(any(Category.class));

        categoryService.deleteCategory("Category");

        verify(categoryRepository, times(1)).findByName("Category");
        verify(productRepository, times(1)).existsByCategory(any(Category.class));
        verify(categoryRepository, times(1)).delete(any(Category.class));
    }

    @Test
    void deleteCategoryThrowTest() {
        when(categoryRepository.findByName("Category")).thenReturn(Optional.of(category));
        when(productRepository.existsByCategory(category)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory("Category"))
                .isInstanceOf(EntityInUseException.class);

        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
