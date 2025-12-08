package dev.sethan8r.mama.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sethan8r.mama.dto.PicturesWorksRequestDto;
import dev.sethan8r.mama.dto.PicturesWorksResponseAdminDto;
import dev.sethan8r.mama.service.PicturesWorksService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(PicturesWorksController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PicturesWorksControllerTest {

    @MockitoBean
    private PicturesWorksService picturesWorksService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createPicturesWorksTest() throws Exception {
        PicturesWorksRequestDto dto = new PicturesWorksRequestDto("Name", "Description");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{1, 2, 3, 4, 5}
        );

        // DTO как JSON-часть multipart-запроса
        MockMultipartFile dtoFile = new MockMultipartFile(
                "dto",                                  // ← имя части (как в @RequestPart("dto"))
                "",                                     // ← filename (не нужен для JSON)
                MediaType.APPLICATION_JSON_VALUE,       // ← Content-Type
                objectMapper.writeValueAsBytes(dto)     // ← сериализуем DTO в JSON
        );

        mockMvc.perform(multipart("/api/pictures/works/admin")
                        .file(file)
                        .file(dtoFile))  // ← добавляем DTO как файл
                .andExpect(status().isCreated());

        verify(picturesWorksService).createPicturesWorks(any(PicturesWorksRequestDto.class),
                any(MultipartFile.class));
    }

    @Test
    void getAllPicturesWorksTest() throws Exception {
        PicturesWorksResponseAdminDto dto1 = new PicturesWorksResponseAdminDto(1L, "/api/picture/works/1/image",
                "Name", "Description");
        PicturesWorksResponseAdminDto dto2 = new PicturesWorksResponseAdminDto(2L, "/api/picture/works/2/image",
                "Name2", "Description2");

        Pageable pageable = PageRequest.of(0, 2);
        Page<PicturesWorksResponseAdminDto> pages = new PageImpl<>(List.of(dto1,
                dto2), pageable, 2);

        when(picturesWorksService.getAllPicturesWorksAdmin(any(Pageable.class))).thenReturn(pages);

        mockMvc.perform(get("/api/pictures/works/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        verify(picturesWorksService).getAllPicturesWorksAdmin(captor.capture());

        Pageable res = captor.getValue();
        assertThat(res.getPageNumber()).isEqualTo(0);
        assertThat(res.getPageSize()).isEqualTo(9);      // ← из @PageableDefault
        assertThat(res.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void deletePicturesWorksTest() throws Exception {
        mockMvc.perform(delete("/api/pictures/works/admin/1"))
                .andExpect(status().isNoContent());

        verify(picturesWorksService).deletePicturesWorks(1L);
    }

    @Test
    void getPictureWorksById() throws Exception {
        byte[] imageBytes = {1, 2, 3, 4, 5};

        when(picturesWorksService.getPictureWorksById(1L)).thenReturn(imageBytes);

        mockMvc.perform(get("/api/pictures/works/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(imageBytes));

        verify(picturesWorksService).getPictureWorksById(1L);
    }
}
