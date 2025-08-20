package ru.rustam.catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "catalog")
public class CatalogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Size(min = 3, max = 15)
    private String name;
    @Size(max = 200)
    private String description;
    @Positive
    @Digits(integer=8, fraction = 2)
    private BigDecimal price;
    @OneToMany(cascade = CascadeType.ALL,
                mappedBy = "catalog")
    private List<FileEntity> images = new ArrayList<>();// ImageEntity BeDirectional связь.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_image_id")
    private FileEntity primaryImage; // перенсти сущность FileEntity , оставить ссылку на FileEntity


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    public CatalogEntity(int id, String name, String description, BigDecimal price) {
    }
}

