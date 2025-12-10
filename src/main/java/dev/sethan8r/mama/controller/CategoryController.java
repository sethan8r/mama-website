package dev.sethan8r.mama.controller;

import dev.sethan8r.mama.dto.CategoryRequestDto;
import dev.sethan8r.mama.dto.CategoryResponseDto;
import dev.sethan8r.mama.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
@Slf4j
public class CategoryController {

    private final CategoryService  categoryService;

    @PostMapping("/admin")
    public ResponseEntity<Void> createCategory(@RequestBody @Valid CategoryRequestDto dto) {
        log.info("POST /api/category/admin called");
        categoryService.createCategory(dto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/admin/{categoryName}")
    public ResponseEntity<Void> updateCategoryName(@PathVariable String categoryName,@RequestParam String newName) {
        categoryService.updateCategoryName(categoryName, newName);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<CategoryResponseDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @DeleteMapping("/admin/{categoryName}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String categoryName) {
        categoryService.deleteCategory(categoryName);

        return ResponseEntity.noContent().build();
    }
}
