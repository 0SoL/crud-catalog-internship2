package ru.rustam.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ru.rustam.catalog.dto.FilteredCatalogDto;
import ru.rustam.catalog.entity.CatalogEntity;
import ru.rustam.catalog.entity.CategoryEntity;
import ru.rustam.catalog.entity.FileEntity;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CatalogNewRepository {
    private final JdbcTemplate jdbcTemplate;

    public CatalogNewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Page<CatalogEntity> searchProduct(FilteredCatalogDto filteredCatalogDto, Pageable pageable) {
        StringBuilder where = new StringBuilder(" WHERE 1=1 "); // читерное условие какое то спрсоить у Данияра
        List<Object> params = new ArrayList<>();

        if (StringUtils.hasText(filteredCatalogDto.getName())) {
            where.append(" AND (LOWER(c.name) LIKE LOWER(?) OR LOWER(c.description) LIKE LOWER(?)) ");
            params.add("%" + filteredCatalogDto.getName() + "%");
            params.add("%" + filteredCatalogDto.getName() + "%");
        }

        if (filteredCatalogDto.getMinPrice() != null) {
            where.append(" AND c.price >= ? ");
            params.add(filteredCatalogDto.getMinPrice());
        }

        if (filteredCatalogDto.getMaxPrice() != null) {
            where.append(" AND c.price <= ? ");
            params.add(filteredCatalogDto.getMaxPrice());
        }

        if (filteredCatalogDto.getHasImages() != null) {
            if (filteredCatalogDto.getHasImages()) {
                where.append(" AND ? = TRUE AND EXISTS (SELECT 1 FROM image i WHERE i.catalog_id = c.id) ");
                params.add(filteredCatalogDto.getHasImages());
            } else {
                where.append(" AND ? = FALSE AND NOT EXISTS (SELECT 1 FROM image i WHERE i.catalog_id = c.id) ");
                params.add(filteredCatalogDto.getHasImages());
            }
        }

        String countSql = "SELECT COUNT(*) FROM catalog c " + where;
        Long total = jdbcTemplate.queryForObject(countSql, params.toArray(), Long.class);

        System.out.println("TOTAL: " + total);

        if (total == null) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        StringBuilder selectSql = new StringBuilder(
                "SELECT c.* " +
                "FROM catalog c " +
                where
        );

        selectSql.append(" LIMIT ? OFFSET ? ");
        List<Object> dataParams = new ArrayList<>(params);
        dataParams.add(pageable.getPageSize());
        dataParams.add((int) pageable.getOffset());

        List<CatalogEntity> content = jdbcTemplate.query(
                selectSql.toString(),
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

                    List<FileEntity> images = jdbcTemplate.query(
                            "SELECT id,name FROM image WHERE catalog_id = ?",
                            (rsImg, rowNumImg) -> {
                                FileEntity fileEntity = new FileEntity();
                                fileEntity.setId(rsImg.getInt("id"));
                                fileEntity.setName(rsImg.getString("name"));
                                return fileEntity;
                            },catalogEntity.getId()
                    );
                    catalogEntity.setImages(images);
                    return catalogEntity;

                },dataParams.toArray()
        );

        System.out.println("DATAPARAMS: " + dataParams);
        System.out.println("PARAMS: " + params);
        System.out.println("CONTENT: " + content);
        System.out.println("PAGEABLE: " + pageable);
        System.out.println("TOTAL: " + total);
        return new PageImpl<>(content, pageable, total);
    }
}
