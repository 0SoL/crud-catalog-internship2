package ru.rustam.catalog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.rustam.catalog.dto.FileDto;
import ru.rustam.catalog.entity.FileEntity;
import ru.rustam.catalog.mapper.FileMapper;
import ru.rustam.catalog.repository.FileRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileService {
    private final FileRepository fileRepository;
    @Value("${file.upload-path}")
    private String folderPath;
    private final FileMapper fileMapper;

    public FileService(FileRepository fileRepository, FileMapper fileMapper) {
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
    }

    public FileDto save(MultipartFile file) throws IOException {
        if (file.getSize() > 2621440 / 2) { // 2.5мб
            throw new IllegalArgumentException("Размер файла слишком большой! " + file.getOriginalFilename());
        };
        if (!List.of("image/jpeg", "image/png", "image/gif").contains(file.getContentType())) {
            throw new IllegalArgumentException("Не поддерживаемый тип файлы (только jpeg,png,gif): "  + file.getContentType());
        }
        FileEntity fileEntity = new FileEntity();
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String fileName = UUID.randomUUID().toString() + extension;
        String filePath = folderPath + "/" + fileName;
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setType(file.getContentType());
        fileEntity.setFilepath(filePath);
        fileEntity = fileRepository.save(fileEntity);
        return fileMapper.toDto(fileEntity);
    };
}
