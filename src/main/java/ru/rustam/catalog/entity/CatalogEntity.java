package ru.rustam.catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
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
    private List<FileEntity> images = new ArrayList<>();
    @Column(name="primary_image") // ImageEntity BeDirectional связь.
    private Integer primaryImage;
}
// primary image id ссылка на main image
//
