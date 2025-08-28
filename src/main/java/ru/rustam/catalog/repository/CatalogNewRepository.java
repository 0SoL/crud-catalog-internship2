package ru.rustam.catalog.repository;

import lombok.extern.slf4j.Slf4j;
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
import ru.rustam.catalog.exception.FileException;

import java.sql.ResultSet;
import java.util.*;

@Slf4j
@Repository
public class CatalogNewRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String BASE_SELECT = "SELECT c.* FROM catalog c ";
    private static final String COUNT_SELECT = "SELECT COUNT(*) FROM catalog c ";
    private static final Map<String, String> SORT_MAP = Map.of(
        "name", "c.name",
        "price" , "c.price"
    );


    public CatalogNewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Page<CatalogEntity> searchProduct(FilteredCatalogDto filteredCatalogDto, Pageable pageable) {
        FilterQuery q = buildFilterQuery(filteredCatalogDto);

        if (q.total == 0) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        StringBuilder selectSql = new StringBuilder(BASE_SELECT).append(q.whereSql);

        if (pageable.getSort().isSorted()) {
            selectSql.append("ORDER BY ");
            List<String> sort = new ArrayList<>();

            for (Sort.Order item : pageable.getSort()) {
                String property = item.getProperty();

                String column = SORT_MAP.get(property);
                if (column != null) {
                    sort.add(column + " " + item.getDirection());
                } else {
                    throw new FileException("Ошибка в фильтре");
                }
            }
            selectSql.append(String.join(", ", sort));
        }

        selectSql.append(" LIMIT ? OFFSET ?");
        q.params.add(pageable.getPageSize());
        q.params.add((int) pageable.getOffset());

        List<CatalogEntity> content = loadPage(selectSql, q.params);
        mapImages(content);

        return new PageImpl<>(content, pageable, q.total);
    }

    private FilterQuery buildFilterQuery(FilteredCatalogDto filteredCatalogDto) {
        List<Object> params = new ArrayList<>();
        List<String> filters = new ArrayList<>();

        if (StringUtils.hasText(filteredCatalogDto.getName())) {
            filters.add("(c.name ILIKE ? OR c.description ILIKE ?)");
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

        String whereSql = filters.isEmpty() ? "" : "WHERE " + String.join(" AND ", filters);

        StringBuilder countSql = new StringBuilder(COUNT_SELECT).append(whereSql);
        Long total = jdbcTemplate.queryForObject(countSql.toString(), params.toArray() ,Long.class);

        return new FilterQuery(whereSql, params, total);
    };

    private List<CatalogEntity> loadPage(StringBuilder selectSql, List<Object> params) {
        return jdbcTemplate.query(
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
                    return catalogEntity;

                },
                params.toArray()
        );
    }

    private void mapImages(List<CatalogEntity> content) {
        if (content.isEmpty()) return;

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
    }

    private record FilterQuery(String whereSql, List<Object> params, Long total) {}
}
