package dev.sethan8r.mama.controller;

import dev.sethan8r.mama.dto.PicturesWorksRequestDto;
import dev.sethan8r.mama.dto.PicturesWorksResponseAdminDto;
import dev.sethan8r.mama.dto.PicturesWorksResponseDto;
import dev.sethan8r.mama.service.PicturesWorksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pictures/works")
public class PicturesWorksController {

    private final PicturesWorksService picturesWorksService;

    @PostMapping(value = "/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //
    public ResponseEntity<Void> createPicturesWorks(@RequestPart("dto") @Valid PicturesWorksRequestDto dto,
                                                    @RequestPart("file") MultipartFile file) {
        picturesWorksService.createPicturesWorks(dto, file);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public Page<PicturesWorksResponseDto> getAllPicturesWorks(
            @PageableDefault(size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return picturesWorksService.getAllPicturesWorks(pageable);
    }

    @GetMapping("/admin")
    public Page<PicturesWorksResponseAdminDto> getAllPicturesWorksAdmin(
            @PageableDefault(size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return picturesWorksService.getAllPicturesWorksAdmin(pageable);
    }

    @DeleteMapping("/admin/{pictureId}")
    public ResponseEntity<Void> deletePicturesWorks(@PathVariable Long pictureId) {
        picturesWorksService.deletePicturesWorks(pictureId);

        return  ResponseEntity.noContent().build();
    }

    @GetMapping("/{pictureId}/image")
    public ResponseEntity<byte[]> getPictureWorksById(@PathVariable Long pictureId) {
        byte[] image = picturesWorksService.getPictureWorksById(pictureId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(image);
    }
}
