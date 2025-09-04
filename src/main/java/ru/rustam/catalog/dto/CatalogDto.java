package ru.rustam.catalog.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogDto {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer primaryImageId;
    private List<FileDto> images;
    private Integer category;
}
// pageable в requestparam

