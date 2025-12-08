package dev.sethan8r.mama.repository;

import dev.sethan8r.mama.model.PicturesProduct;
import dev.sethan8r.mama.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PicturesProductRepository extends CrudRepository<PicturesProduct, Long> {
    Optional<PicturesProduct> findFirstByProductAndSortOrder(Product product, Integer sortOrder);
    List<PicturesProduct> findAllByProductIdOrderBySortOrderAsc(Long productId);
    List<PicturesProduct> findByProductIdInAndSortOrder(List<Long> productId, Integer sortOrder);

}
