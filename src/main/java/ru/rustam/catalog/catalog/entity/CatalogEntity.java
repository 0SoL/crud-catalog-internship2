package ru.rustam.catalog.catalog.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogEntity {
    @Id
    @GeneratedValue
    int id;
    String name;
    String description;
    double price;
}
