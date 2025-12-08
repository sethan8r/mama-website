package dev.sethan8r.mama.it;

import dev.sethan8r.mama.dto.PicturesWorksRequestDto;
import dev.sethan8r.mama.model.PicturesWorks;
import dev.sethan8r.mama.repository.PicturesWorksRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

public class PicturesWorksControllerTestIT extends BaseIntegrationTest {

    @Autowired
    private PicturesWorksRepository picturesWorksRepository;

    PicturesWorks getId;

    @BeforeEach
    public void setup() {
        picturesWorksRepository.deleteAll();

        var bytes = "test image content".getBytes();

        PicturesWorks picturesWorks = new PicturesWorks();
        picturesWorks.setName("Name");
        picturesWorks.setDescription("Description");
        picturesWorks.setImage(bytes);

        getId = picturesWorksRepository.save(picturesWorks);
    }

    @Test
    void createPicturesWorksTest() {
        byte[] fileContent = "fake image content".getBytes();
        var file = new ByteArrayResource(fileContent) {
            @Override
            public String getFilename() {
                return "test-image.jpg";
            }
        };

        var body = new LinkedMultiValueMap<String, Object>();
        body.add("dto", new PicturesWorksRequestDto("Picture", "Description"));
        body.add("file", file);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var response = restTemplate.postForEntity(
                "/api/pictures/works/admin",
                new HttpEntity<>(body, headers),
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var res = picturesWorksRepository.findById(2L).orElse(null);

        assertThat(res).isNotNull();
        assertThat(res.getName()).isEqualTo("Picture");
        assertThat(res.getDescription()).isEqualTo("Description");
        assertThat(res.getImage()).isEqualTo("fake image content".getBytes());
    }

    @Test
    void deletePicturesWorksTest() {
        var response = restTemplate.exchange(
                "/api/pictures/works/admin/1",
                HttpMethod.DELETE,
                null,
                Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        var res = picturesWorksRepository.findById(1L).orElse(null);

        assertThat(res).isNull();
    }

    @Test
    void getPictureWorksByIdTest() {
        var response = restTemplate.exchange(
                "/api/pictures/works/" + getId.getId() + "/image",
                HttpMethod.GET,
                null,
                byte[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        var res = response.getBody();

        assertThat(res).isNotNull()
                .isEqualTo("test image content".getBytes());
    }
}