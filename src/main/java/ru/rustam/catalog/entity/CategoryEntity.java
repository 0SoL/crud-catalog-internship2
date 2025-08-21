package ru.rustam.catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Size(max=25)
    @NotNull
    private String name;
    @OneToMany(cascade = CascadeType.ALL,
    mappedBy = "category", orphanRemoval = true)
    private List<CatalogEntity> catalogs;
}
