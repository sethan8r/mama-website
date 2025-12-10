package dev.sethan8r.mama.repository;

import dev.sethan8r.mama.model.PicturesProduct;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PicturesProductRepository extends CrudRepository<PicturesProduct, Long> {
    List<PicturesProduct> findAllByProductIdOrderBySortOrderAsc(Long productId);
    List<PicturesProduct> findByProductIdInAndSortOrder(List<Long> productId, Integer sortOrder);
    List<PicturesProduct> findAllByProductSlugOrderBySortOrderAsc(String slug);
    List<PicturesProduct> findByProductSlugInAndSortOrder(List<String> slug, Integer sortOrder);
}
