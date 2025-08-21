package ru.rustam.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.rustam.catalog.entity.CatalogEntity;

import java.math.BigDecimal;
import java.util.List;


public interface CatalogRepository extends JpaRepository<CatalogEntity, Integer>{
    @Query(value = """ 
    SELECT c.* FROM catalog c
    WHERE (
        :name IS NULL
        OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))
        OR LOWER(c.description) LIKE LOWER(CONCAT('%', :name, '%'))
    )
    AND (:minPrice IS NULL OR c.price >= :minPrice)
    AND (:maxPrice IS NULL OR c.price <= :maxPrice)
    AND (
        :hasImage IS NULL
        OR (:hasImage = TRUE  AND EXISTS (SELECT 1 FROM image i WHERE i.catalog_id = c.id))
        OR (:hasImage = FALSE AND NOT EXISTS (SELECT 1 FROM image i WHERE i.catalog_id = c.id))
        )
    AND (:categoryId IS NULL OR c.category_id = :categoryId)
    """, nativeQuery = true)
    Page<CatalogEntity> search(@Param("name") String name,
                               @Param("description") String description,
                               @Param("minPrice") BigDecimal minPrice,
                               @Param("maxPrice") BigDecimal maxPrice,
                               @Param("hasImage") Boolean hasImages,
                               @Param("categoryId") Integer categoryId,
                               Pageable pageable);

}