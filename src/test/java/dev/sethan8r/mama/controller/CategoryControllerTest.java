package dev.sethan8r.mama.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sethan8r.mama.dto.CategoryRequestDto;
import dev.sethan8r.mama.dto.CategoryResponseDto;
import dev.sethan8r.mama.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createCategoryTest() throws Exception {
        CategoryRequestDto dto = new CategoryRequestDto("Category");

        mockMvc.perform(post("/api/category/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))).andExpect(status().isCreated());

        verify(categoryService).createCategory(dto);
    }

    @Test
    void updateCategoryNameTest() throws Exception {
        mockMvc.perform(patch("/api/category/admin/Category")
                .param("newName", "NewName"))
                .andExpect(status().isNoContent());

        verify(categoryService).updateCategoryName("Category", "NewName");
    }

    @Test
    void getAllCategoriesTest() throws Exception {
        List<CategoryResponseDto> list = List.of(new CategoryResponseDto("Category"),
                new CategoryResponseDto("Category1"));

        when(categoryService.getAllCategories()).thenReturn(list);

        mockMvc.perform(get("/api/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Category"))
                .andExpect(jsonPath("$[1].name").value("Category1"));

        verify(categoryService).getAllCategories();
    }

    @Test
    void deleteCategoryTest() throws Exception {
        mockMvc.perform(delete("/api/category/admin/Category"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory("Category");
    }
}
