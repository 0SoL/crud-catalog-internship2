package ru.rustam.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ru.rustam.catalog.dto.FilteredCatalogDto;
import ru.rustam.catalog.entity.CatalogEntity;
import ru.rustam.catalog.entity.CategoryEntity;
import ru.rustam.catalog.entity.FileEntity;

import java.sql.ResultSet;
import java.util.*;

@Repository
public class CatalogNewRepository {
    private final JdbcTemplate jdbcTemplate;

    public CatalogNewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final StringBuilder BASE_SELECT = new StringBuilder(
            "SELECT c.* FROM catalog c "
    );

    private static final StringBuilder COUNT_SELECT = new StringBuilder(
            "SELECT COUNT(*) FROM catalog c "
    );

    public Page<CatalogEntity> searchProduct(FilteredCatalogDto filteredCatalogDto, Pageable pageable) {
        List<Object> params = new ArrayList<>();
        List<String> filters = new ArrayList<>();
        if (StringUtils.hasText(filteredCatalogDto.getName())) {
            filters.add("(LOWER(c.name) LIKE LOWER(?) OR LOWER(c.description) LIKE LOWER(?))");
            params.add("%" + filteredCatalogDto.getName() + "%");
            params.add("%" + filteredCatalogDto.getName() + "%");
        }

        if (filteredCatalogDto.getMinPrice() != null) {
            filters.add("c.price >= ?");
            params.add(filteredCatalogDto.getMinPrice());
        }

        if (filteredCatalogDto.getMaxPrice() != null) {
            filters.add("c.price <= ? ");
            params.add(filteredCatalogDto.getMaxPrice());
        }

        if (filteredCatalogDto.getHasImages() != null) {
            if (filteredCatalogDto.getHasImages()) {
                filters.add("EXISTS (SELECT 1 FROM image i WHERE i.catalog_id = c.id)");
            } else {
                filters.add("NOT EXISTS (SELECT 1 FROM image i WHERE i.catalog_id = c.id)");
            }
        }

        String where = filters.isEmpty() ? "" : "WHERE " + String.join(" AND ", filters);

        StringBuilder countSql = new StringBuilder(COUNT_SELECT).append(where);

        Long total = jdbcTemplate.queryForObject(countSql.toString(), params.toArray() ,Long.class);
        if (total == null) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        StringBuilder selectSql = new StringBuilder(BASE_SELECT).append(where);

        System.out.println(selectSql);

        if (pageable.getSort().isSorted()) {
            selectSql.append("ORDER BY ");
            List<String> sort = new ArrayList<>();
            for (Sort.Order item : pageable.getSort()) {
                String property = item.getProperty();
                sort.add(property + " " + item.getDirection());
            }
            selectSql.append(String.join(", ", sort));
        }

        selectSql.append(" LIMIT ? OFFSET ?");
        params.add(pageable.getPageSize());
        params.add((int) pageable.getOffset());
        // map в для поиска

        List<CatalogEntity> content = jdbcTemplate.query(
                selectSql.toString(),
                (rs, rowNum) -> {
                    CatalogEntity catalogEntity = new CatalogEntity();
                    catalogEntity.setId(rs.getInt("id"));
                    catalogEntity.setName(rs.getString("name"));
                    catalogEntity.setDescription(rs.getString("description"));
                    catalogEntity.setPrice(rs.getBigDecimal("price"));

                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getInt("category_id"));
                    catalogEntity.setCategory(categoryEntity);

                    FileEntity primaryImage = new FileEntity();
                    primaryImage.setId(rs.getInt("primary_image_id"));
                    catalogEntity.setPrimaryImage(primaryImage);

                    // Оптимизировать эту часть
//                    List<FileEntity> images = jdbcTemplate.query(
//                            "SELECT id,name FROM image WHERE catalog_id = ?",
//                            (rsImg, rowNumImg) -> {
//                                FileEntity fileEntity = new FileEntity();
//                                fileEntity.setId(rsImg.getInt("id"));
//                                fileEntity.setName(rsImg.getString("name"));
//                                return fileEntity;
//                            },catalogEntity.getId()
//                    );
//                    catalogEntity.setImages(images);
                    return catalogEntity;

                },
                params.toArray()
        );

        if (content.isEmpty()) {
            return new PageImpl<>(content, pageable, total);
        }

        List<Integer> catalogIds = content.stream()
                .map(CatalogEntity::getId)
                .toList();

        String imagesSql = "SELECT id, name, catalog_id FROM image WHERE catalog_id IN (" +
                String.join(",", Collections.nCopies(catalogIds.size(), "?")) + ")";

        Map<Integer, List<FileEntity>> imagesMap = jdbcTemplate.query(
                imagesSql,
                (ResultSet rs) -> {
                    Map<Integer, List<FileEntity>> map = new HashMap<>();
                    while (rs.next()) {
                        int catalogId = rs.getInt("catalog_id");
                        FileEntity fileEntity = new FileEntity();
                        fileEntity.setId(rs.getInt("id"));
                        fileEntity.setName(rs.getString("name"));
                        map.computeIfAbsent(catalogId, k -> new ArrayList<>()).add(fileEntity);
                    }
                    return map;
                },
                catalogIds.toArray()
        );
        if (imagesMap != null) {
            content.forEach(catalog ->
                    catalog.setImages(imagesMap.getOrDefault(catalog.getId(), new ArrayList<>()))
            );
        }
        return new PageImpl<>(content, pageable, total);
    }
}
