package dev.sethan8r.mama.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sethan8r.mama.dto.PicturesProductResponseAdminDto;
import dev.sethan8r.mama.dto.UploadPicturesResponseDto;
import dev.sethan8r.mama.service.PicturesProductService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PicturesProductController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PicturesProductControllerTest {

    @MockitoBean
    private PicturesProductService picturesProductService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createPicturesProductManyTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{1, 2, 3, 4, 5}
        );

        MockMultipartFile sortOrdersPart = new MockMultipartFile(
                "sortOrders",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                "[0,1]".getBytes()
        );

        Integer[] sortOrders = {0, 1};
        UploadPicturesResponseDto dto = new UploadPicturesResponseDto(2, 2);

        when(picturesProductService.createPicturesProductMany(1L, sortOrders,
                new MockMultipartFile[]{file, file})).thenReturn(dto);

        mockMvc.perform(multipart("/api/pictures/product/admin/1/more")
                        .file(file)
                        .file(file)
                        .file(sortOrdersPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uploaded").value(2))
                .andExpect(jsonPath("$.total").value(2));

        verify(picturesProductService).createPicturesProductMany(eq(1L),
                eq(sortOrders), any(MultipartFile[].class));
    }

    @Test
    void createPicturesProductTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{1, 2, 3, 4, 5}
        );

        mockMvc.perform(multipart("/api/pictures/product/admin/1")
                .file(file)
                .param("sortOrders", "0"))
                .andExpect(status().isCreated());

        verify(picturesProductService).createPicturesProduct(1L, 0, file);
    }

    @Test
    void updateSortOrderTest() throws Exception {
        mockMvc.perform(patch("/api/pictures/product/admin/1")
                .param("sortOrder", "0"))
                .andExpect(status().isNoContent());

        verify(picturesProductService).updateSortOrder(1L, 0);
    }

    @Test
    void getPicturesProductByProductIdForAdminTest() throws Exception {
        PicturesProductResponseAdminDto dto = new PicturesProductResponseAdminDto(
                1L, "/api/pictures/product/1/image", 1L, "product", 1);

        when(picturesProductService.getPicturesProductByProductIdForAdmin(1L))
                .thenReturn(List.of(dto, dto));

        mockMvc.perform(get("/api/pictures/product/admin/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].productName").value("product"));
    }

    @Test
    void getPictureByIdTest()  throws Exception {
        var image = new byte[]{1, 2, 3, 4, 5};

        when(picturesProductService.getPictureById(1L)).thenReturn(image);

        mockMvc.perform(get("/api/pictures/product/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(image))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE));

        verify(picturesProductService).getPictureById(1L);
    }
}
