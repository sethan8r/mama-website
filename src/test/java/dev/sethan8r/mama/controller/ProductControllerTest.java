package dev.sethan8r.mama.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sethan8r.mama.dto.*;
import dev.sethan8r.mama.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {
    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createProductTest() throws Exception {
        var resDto = new ProductResponseAdminDto(
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
                new ArrayList<>(List.of()),
                true,
                false,
                "kurtka-white"
        );

        var dto = new ProductRequestDto(
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

        when(productService.createProduct(dto)).thenReturn(resDto);

        mockMvc.perform(post("/api/product/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(resDto)));

        verify(productService).createProduct(dto);
    }

    @Test
    void updateProductPriceTest() throws Exception {
        mockMvc.perform(patch("/api/product/admin/price/1")
                .param("price", "от 5000"))
                .andExpect(status().isNoContent());

        verify(productService).updateProductPrice(1L, "от 5000");
    }

    @Test
    void getAllProductsShortTest() throws Exception {
        var dto = new ProductResponseShortDto(
                "Product",
                "1000",
                "/api/pictures/product/1/image",
                "kurtka-white",
                true,
                false
        );

        Page<ProductResponseShortDto> pages = new PageImpl<>(List.of(dto, dto));

        when(productService.getAllProductsShort(any(Pageable.class))).thenReturn(pages);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        mockMvc.perform(get("/api/product/short"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        verify(productService).getAllProductsShort(captor.capture());

        var res = captor.getValue();

        assertThat(res.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(res.getPageNumber()).isEqualTo(0);
        assertThat(res.getPageSize()).isEqualTo(9);
    }

    @Test
    void getProductBySlugTest() throws Exception {
        var dto = new ProductResponseDto(
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

        when(productService.getProductBySlug("kurtka-white")).thenReturn(dto);

        mockMvc.perform(get("/api/product/kurtka-white"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));

        verify(productService).getProductBySlug("kurtka-white");
    }

    @Test
    void searchProductsByNameTest() throws Exception {
        var dto = new ProductResponseShortDto(
                "Product",
                "1000",
                "/api/pictures/product/1/image",
                "kurtka-white",
                true,
                false
        );

        Page<ProductResponseShortDto> pages = new PageImpl<>(List.of(dto, dto));

        when(productService.searchProductsByName(eq("Product"), any(Pageable.class)))
                .thenReturn(pages); //обязательно eq("Product")

        mockMvc.perform(get("/api/product/search")
                .param("name", "Product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Product"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        verify(productService).searchProductsByName(eq("Product"), captor.capture()); //обязательно eq("Product")

        var res = captor.getValue();

        assertThat(res.getSort()).isEqualTo(Sort.unsorted());
        assertThat(res.getPageNumber()).isEqualTo(0);
        assertThat(res.getPageSize()).isEqualTo(9);
    }
}
