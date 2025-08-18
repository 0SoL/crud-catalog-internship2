package ru.rustam.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rustam.catalog.entity.CatalogEntity;


public interface CatalogRepository extends JpaRepository<CatalogEntity, Integer> {
}
