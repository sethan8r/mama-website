package dev.sethan8r.mama.dto;

public record PicturesProductResponseDto(
        String url
) { } //сортируем список в сервисе и маппим url в mapstuct. Создаем эндпоинт для поиска картинок
//картинки будем возвращать через

//@GetMapping(value = "/pictures/{pictureId}/image", produces = MediaType.IMAGE_JPEG_VALUE)
//public ResponseEntity<byte[]> getPicture(@PathVariable Long pictureId) {
//
//    PicturesProduct pic = picturesProductRepository.findById(pictureId)
//            .orElseThrow(() -> new NotFoundException("Picture not found"));
//
//    return ResponseEntity.ok()
//            .contentType(MediaType.IMAGE_JPEG)
//            .body(pic.getImage());
//} return "/pictures/product/" + picturesProduct.getId() + "/image"; and
//         "/pictures/works/" + picturesWorks.getId() + "/image";