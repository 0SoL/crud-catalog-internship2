package ru.rustam.catalog.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class CatalogDto {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer primaryImageId;
    private List<FileDto> images;
    private Integer category;
}


