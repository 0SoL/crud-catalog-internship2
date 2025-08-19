package ru.rustam.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rustam.catalog.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {

}
