package ru.rustam.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.rustam.catalog.entity.CatalogEntity;

import java.util.List;

public interface CatalogRepository extends JpaRepository<CatalogEntity, Integer> {
    Integer id(Integer id);
    @Query("SELECT product FROM CatalogEntity product LEFT JOIN FETCH product.images image WHERE image.id = product.primaryImage")
    List<CatalogEntity> findPrimaryImage();
}
