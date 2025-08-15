package ru.rustam.catalog.dto;

import jakarta.persistence.Column;
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
    @Column(name="primary_image")
    private Integer primaryImage;
    private List<FileDto> images;
}


