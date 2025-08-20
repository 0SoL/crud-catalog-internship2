package ru.rustam.catalog.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.rustam.catalog.entity.CatalogEntity;
import ru.rustam.catalog.entity.CategoryEntity;
import ru.rustam.catalog.entity.FileEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CatalogNewRepository {
    private final JdbcTemplate jdbcTemplate;

    public CatalogNewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CatalogEntity> searchProduct(String name, BigDecimal minPrice, BigDecimal maxPrice,Boolean hasImages, int page, int size) {
        StringBuilder sqlQuery = new StringBuilder("SELECT c.* FROM catalog c WHERE");
        List<Object> params = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            sqlQuery.append(" (LOWER(c.name) LIKE LOWER(?) OR LOWER(c.description) LIKE LOWER(?)) ");
            params.add("%" + name + "%");
            params.add("%" + name + "%");
        }

        if (minPrice != null) {
            sqlQuery.append(" c.price >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sqlQuery.append(" c.price <= ?");
            params.add(maxPrice);
        }

        if (hasImages != null) {
            sqlQuery.append(" ? = TRUE AND EXISTS (SELECT 1 FROM image i WHERE i.catalog_id = c.id)");
            params.add(hasImages);
        }

        sqlQuery.append(" LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page-1)*size);

        return jdbcTemplate.query(
                sqlQuery.toString(),
                (rs, rowNum) -> {
                    CatalogEntity catalogEntity = new CatalogEntity();
                    CategoryEntity categoryEntity = new CategoryEntity();
                    FileEntity primaryImage = new FileEntity();
                    catalogEntity.setId(rs.getInt("id"));
                    catalogEntity.setName(rs.getString("name"));
                    catalogEntity.setDescription(rs.getString("description"));
                    catalogEntity.setPrice(rs.getBigDecimal("price"));

                    categoryEntity.setId(rs.getInt("category_id"));
                    catalogEntity.setCategory(categoryEntity);

                    primaryImage.setId(rs.getInt("primary_image_id"));
                    catalogEntity.setPrimaryImage(primaryImage);

                    catalogEntity.setImages(new ArrayList<>());
                    return catalogEntity;
                },params.toArray()
        );
    }
}
