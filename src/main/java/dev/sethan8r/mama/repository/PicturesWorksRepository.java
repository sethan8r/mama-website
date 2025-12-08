package dev.sethan8r.mama.repository;

import dev.sethan8r.mama.model.PicturesWorks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface PicturesWorksRepository extends CrudRepository<PicturesWorks, Long> {
    Page<PicturesWorks> findAll(Pageable pageable);
}
