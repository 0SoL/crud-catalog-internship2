package ru.rustam.catalog.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.rustam.catalog.entity.CatalogEntity;
import ru.rustam.catalog.entity.CategoryEntity;
import ru.rustam.catalog.entity.FileEntity;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilterMapper implements RowMapper<CatalogEntity> {
    @Override
    public CatalogEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
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

        Array array = rs.getArray("image_ids");
        Integer[] ids = (Integer[]) array.getArray();
        List<FileEntity> images = new ArrayList<>();
        for (Integer id : ids) {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setId(id);
            images.add(fileEntity);
        }
        catalogEntity.setImages(images);
        return catalogEntity;
    }
}
