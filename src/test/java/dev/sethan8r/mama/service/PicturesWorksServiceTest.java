package dev.sethan8r.mama.service;

import dev.sethan8r.mama.dto.PicturesWorksRequestDto;
import dev.sethan8r.mama.dto.PicturesWorksResponseAdminDto;
import dev.sethan8r.mama.dto.PicturesWorksResponseDto;
import dev.sethan8r.mama.exception.InvalidRequestException;
import dev.sethan8r.mama.mapper.PicturesWorksMapper;
import dev.sethan8r.mama.model.PicturesWorks;
import dev.sethan8r.mama.repository.PicturesWorksRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class PicturesWorksServiceTest {

    @Mock
    private PicturesWorksMapper picturesWorksMapper;

    @Mock
    private PicturesWorksRepository picturesWorksRepository;

    @InjectMocks
    private PicturesWorksService picturesWorksService;

    PicturesWorks picturesWorks;
    PicturesWorksRequestDto picturesWorksRequestDto;
    PicturesWorksResponseAdminDto picturesWorksResponseAdminDto;
    PicturesWorksResponseDto  picturesWorksResponseDto;
    byte[] testByte;

    @BeforeEach
    public void setUp(){
        testByte = new byte[]{1, 2, 3, 4, 5};

        picturesWorks = new PicturesWorks();
        picturesWorks.setId(1L);
        picturesWorks.setName("Name");
        picturesWorks.setDescription("Description");
        picturesWorks.setImage(testByte);

        picturesWorksRequestDto = new PicturesWorksRequestDto("Name", "Description");

        picturesWorksResponseAdminDto = new PicturesWorksResponseAdminDto(1L, "/api/picture/works/1/image",
                "Name", "Description");

        picturesWorksResponseDto = new PicturesWorksResponseDto("/api/picture/works/1/image", "Name",
                "Description");
    }

    @Test
    void createPicturesWorksTest() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                testByte
        );

        when(picturesWorksMapper.toPicturesWorks(picturesWorksRequestDto, file.getBytes()))
                .thenReturn(picturesWorks);
        when(picturesWorksRepository.save(any(PicturesWorks.class))).thenReturn(picturesWorks);

        ArgumentCaptor<PicturesWorks> captor = ArgumentCaptor.forClass(PicturesWorks.class);

        picturesWorksService.createPicturesWorks(picturesWorksRequestDto, file);

        verify(picturesWorksMapper, times(1))
                .toPicturesWorks(picturesWorksRequestDto, file.getBytes());
        verify(picturesWorksRepository, times(1)).save(captor.capture());

        var res = captor.getValue();

        assertThat(testByte).isEqualTo(res.getImage());
        assertThat("Name").isEqualTo(res.getName());
        assertThat("Description").isEqualTo(res.getDescription());
    }

    @Test
    void createPicturesWorksThrowTest() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                new byte[0]
        );

        assertThatThrownBy(() ->
                picturesWorksService.createPicturesWorks(picturesWorksRequestDto, file))
                .isInstanceOf(InvalidRequestException.class);

    }

    @Test
    void getAllPicturesWorks() {
        PicturesWorksResponseDto  picturesWorksResponseDto2 = new PicturesWorksResponseDto(
                "/api/picture/works/2/image", "Name2", "Description2");

        Pageable pageable = PageRequest.of(0, 2);

        Page<PicturesWorks> pages = new PageImpl<>(List.of(picturesWorks,
                picturesWorks));

        when(picturesWorksRepository.findAll(pageable)).thenReturn(pages);
        when(picturesWorksMapper.toPicturesWorksResponseDto(any(PicturesWorks.class)))
                .thenReturn(picturesWorksResponseDto, picturesWorksResponseDto2);

        var res = picturesWorksService.getAllPicturesWorks(pageable);

        verify(picturesWorksRepository, times(1)).findAll(pageable);
        verify(picturesWorksMapper, times(2))
                .toPicturesWorksResponseDto(any(PicturesWorks.class));

        assertThat(res.getTotalElements()).isEqualTo(2);
        assertThat(res.getContent().getFirst()).isEqualTo(picturesWorksResponseDto);
        assertThat(res.getContent().get(1)).isEqualTo(picturesWorksResponseDto2);
    }

    @Test
    void getPictureWorksByIdTest() {
        when(picturesWorksRepository.findById(1L)).thenReturn(Optional.of(picturesWorks));

        var res =  picturesWorksService.getPictureWorksById(1L);

        verify(picturesWorksRepository, times(1)).findById(1L);

        assertThat(res).isEqualTo(picturesWorks.getImage());
    }
}
