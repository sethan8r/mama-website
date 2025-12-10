package dev.sethan8r.mama.repository;

import dev.sethan8r.mama.model.Category;
import dev.sethan8r.mama.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCaseAndDeletedFalse(String name,  Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName AND p.deleted = false")
    Page<Product> findByCategoryNameAndIsDeletedFalse(@Param("categoryName") String categoryName, Pageable pageable);

    Page<Product> findByDeletedFalse(Pageable pageable);


    @EntityGraph(attributePaths = "picturesProduct")
    Optional<Product> findBySlug(String slug);

    Page<Product> findByNameContainingIgnoreCase(String name,  Pageable pageable);

    boolean existsByCategory(Category category);

    boolean existsBySlug(String slug);
}
