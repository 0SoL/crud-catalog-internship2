package ru.rustam.catalog.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.rustam.catalog.dto.CatalogDto;
import ru.rustam.catalog.entity.CatalogEntity;

import java.util.List;

public interface CatalogRepository extends JpaRepository<CatalogEntity, Integer>, JpaSpecificationExecutor<CatalogEntity> {
    
}
