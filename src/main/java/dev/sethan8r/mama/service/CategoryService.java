package dev.sethan8r.mama.service;

import dev.sethan8r.mama.dto.CategoryRequestDto;
import dev.sethan8r.mama.dto.CategoryResponseDto;
import dev.sethan8r.mama.exception.AlreadyExistException;
import dev.sethan8r.mama.exception.EntityInUseException;
import dev.sethan8r.mama.exception.NotFoundException;
import dev.sethan8r.mama.mapper.CategoryMapper;
import dev.sethan8r.mama.model.Category;
import dev.sethan8r.mama.repository.CategoryRepository;
import dev.sethan8r.mama.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    @Transactional
    public void createCategory(CategoryRequestDto dto) {
        log.info("Создание категории с name={}", dto.name());

        if (categoryRepository.existsByName(dto.name())) {
            throw new AlreadyExistException("Категория с именем \"" + dto.name() + "\" уже существует");
        }

        Category category = categoryMapper.toCategory(dto);

        categoryRepository.save(category);
    }

    @Transactional
    public void updateCategoryName(String categoryName, String newName) {
        log.info("Изменение имени категории с {} на {}", categoryName, newName);

        if (categoryRepository.existsByName(newName)) {
            throw new AlreadyExistException("Категория с именем \"" + newName + "\" уже существует");
        }

        Category category = categoryRepository.findByName(categoryName).orElseThrow(
                () -> new NotFoundException("Категория с именем \"" + categoryName + "\" не найдена"));
        category.setName(newName);

        categoryRepository.save(category);
    }

    public List<CategoryResponseDto> getAllCategories() {
        log.info("Вывод списка всех категорий");

        List<Category> categoryList = categoryRepository.findAll();

        return categoryList.stream()
                .map(categoryMapper::toCategoryResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCategory(String categoryName) {
        log.info("Удаление категории {}", categoryName);

        Category category = categoryRepository.findByName(categoryName).orElseThrow(
                () -> new NotFoundException("Категория с именем \"" + categoryName + "\" не найдена"));

        if (productRepository.existsByCategory(category)) {
            throw new EntityInUseException("Нельзя удалить категорию \"" + categoryName + "\" — к ней привязаны товары");
        }

        categoryRepository.delete(category);
    }
}
