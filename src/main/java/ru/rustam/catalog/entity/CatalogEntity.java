package ru.rustam.catalog.entity;

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
    private int id;
    private String name;
    private String description;
    private double price;
}
