package dev.sethan8r.mama.controller;

import dev.sethan8r.mama.dto.ProductRequestDto;
import dev.sethan8r.mama.dto.ProductResponseAdminDto;
import dev.sethan8r.mama.dto.ProductResponseDto;
import dev.sethan8r.mama.dto.ProductResponseShortDto;
import dev.sethan8r.mama.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/admin")
    public ResponseEntity<ProductResponseAdminDto> createProduct(@RequestBody @Valid ProductRequestDto dto) {
        var res = productService.createProduct(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping("/admin/price/{productId}")
    public ResponseEntity<Void> updateProductPrice(@PathVariable Long productId, @RequestParam String price) {
        productService.updateProductPrice(productId, price);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/short")
    public Page<ProductResponseShortDto> getAllProductsShort(
            @PageableDefault(size = 9, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return productService.getAllProductsShort(pageable);
    }

    @GetMapping("/admin/{slug}")
    public ProductResponseAdminDto getProductBySlugForAdmin(@PathVariable String slug) {
        return productService.getProductBySlugForAdmin(slug);
    }

    @GetMapping("/{slug}")
    public ProductResponseDto getProductBySlug(@PathVariable String slug) {
        return productService.getProductBySlug(slug);
    }

    @GetMapping
    public Page<ProductResponseShortDto> getProductsByCategoryName(
            @RequestParam String categoryName,
            @PageableDefault(size = 9, sort = "id", direction = Sort.Direction.DESC)  Pageable pageable) {
        return productService.getProductsByCategoryName(categoryName, pageable);
    }

    @GetMapping("/search")
    public Page<ProductResponseShortDto> searchProductsByName(
            @RequestParam String name,
            @PageableDefault(size = 9) Pageable pageable) {
        return productService.searchProductsByName(name, pageable);
    }

    @GetMapping("/admin/search")
    public Page<ProductResponseShortDto> searchProductsByNameForAdmin(
            @RequestParam String name,
            @PageableDefault(size = 9) Pageable pageable) {
        return productService.searchProductsByNameForAdmin(name, pageable);
    }

    @PatchMapping("/admin/available/{productId}")
    public ResponseEntity<Void> updateProductIsAvailable(@PathVariable Long productId) {
        productService.updateProductIsAvailable(productId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/deleted/{productId}")
    public ResponseEntity<Void> updateProductIsDeleted(@PathVariable Long productId) {
        productService.updateProductIsDeleted(productId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);

        return ResponseEntity.noContent().build();
    }
}
