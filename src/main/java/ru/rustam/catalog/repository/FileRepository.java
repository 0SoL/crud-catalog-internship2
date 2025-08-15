package ru.rustam.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.rustam.catalog.entity.FileEntity;


public interface FileRepository extends JpaRepository<FileEntity, Integer> {
}
