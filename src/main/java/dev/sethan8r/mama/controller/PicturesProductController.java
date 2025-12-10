package dev.sethan8r.mama.controller;

import dev.sethan8r.mama.dto.PicturesProductResponseAdminDto;
import dev.sethan8r.mama.dto.UploadPicturesResponseDto;
import dev.sethan8r.mama.service.PicturesProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pictures/product")
public class PicturesProductController {

    private final PicturesProductService picturesProductService;

    @PostMapping(value ="/admin/{productId}/more", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //
    public ResponseEntity<UploadPicturesResponseDto> createPicturesProductMany(
            @PathVariable Long productId, @RequestPart("sortOrders") Integer[] sortOrders,
            @RequestPart("files") MultipartFile[] files) {
        UploadPicturesResponseDto result =
                picturesProductService.createPicturesProductMany(productId, sortOrders, files);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping(value ="/admin/{slug}/more", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //
    public ResponseEntity<UploadPicturesResponseDto> createPicturesProductBySlugMany(
            @PathVariable String slug, @RequestPart("sortOrders") Integer[] sortOrders,
            @RequestPart("files") MultipartFile[] files) {
        UploadPicturesResponseDto result =
                picturesProductService.createPicturesProductBySlugMany(slug, sortOrders, files);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/admin/{productId}")
    public ResponseEntity<Void> createPicturesProduct(@PathVariable Long productId,@RequestParam Integer sortOrders,
                                                      @RequestPart("file") MultipartFile file) {
        picturesProductService.createPicturesProduct(productId, sortOrders, file);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/admin/{slug}")
    public ResponseEntity<Void> createPicturesProductBySlug(@PathVariable String slug,@RequestParam Integer sortOrders,
                                                      @RequestPart("file") MultipartFile file) {
        picturesProductService.createPicturesProductBySlug(slug, sortOrders, file);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/admin/{pictureId}")
    public ResponseEntity<Void> updateSortOrder(@PathVariable Long pictureId,@RequestParam Integer sortOrder) {
        picturesProductService.updateSortOrder(pictureId, sortOrder);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/{productId}")
    public List<PicturesProductResponseAdminDto> getPicturesProductByProductIdForAdmin(@PathVariable Long productId) {
        return picturesProductService.getPicturesProductByProductIdForAdmin(productId);
    }

    @DeleteMapping("/admin/{pictureId}")
    public ResponseEntity<Void> deletePicturesProductById(@PathVariable Long pictureId) {
        picturesProductService.deletePicturesProductById(pictureId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{pictureId}/image")
    public ResponseEntity<byte[]> getPictureById(@PathVariable Long pictureId) {
        byte[] image = picturesProductService.getPictureById(pictureId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(image);
    }
}
