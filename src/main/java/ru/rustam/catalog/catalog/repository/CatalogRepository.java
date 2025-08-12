package ru.rustam.catalog.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rustam.catalog.catalog.entity.CatalogEntity;

public interface CatalogRepository extends JpaRepository<CatalogEntity, Integer> {
}
