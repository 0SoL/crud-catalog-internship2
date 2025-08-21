package ru.rustam.catalog.mapper;

import org.springframework.stereotype.Component;
import ru.rustam.catalog.dto.FileDto;
import ru.rustam.catalog.entity.FileEntity;

@Component
public class FileMapper {
    public FileDto toDto(FileEntity e) {
        FileDto dto = new FileDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
//        dto.setType(e.getType());
//        dto.setFilepath(e.getFilepath());
        return dto;
    }
}

