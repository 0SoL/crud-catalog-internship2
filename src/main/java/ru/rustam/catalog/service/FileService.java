package ru.rustam.catalog.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.rustam.catalog.dto.FileDto;
import ru.rustam.catalog.entity.FileEntity;
import ru.rustam.catalog.exception.FileHandlerException;
import ru.rustam.catalog.mapper.FileMapper;
import ru.rustam.catalog.repository.FileRepository;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;
    @Value("${file.upload-path}")
    private String folderPath;

    @Autowired
    public FileService(FileRepository fileRepository, FileMapper fileMapper) {
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
    }

    public FileDto save(MultipartFile file) {
        int maxByte = 2621440;
        if (file.getSize() > maxByte) {
            throw new FileHandlerException("Размер файла слишком большой! " + file.getOriginalFilename());
        }
        if (!ImageType.isImageType(file.getContentType())) {
            throw new FileHandlerException("Не поддерживаемый тип файла! "  + file.getContentType());
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
    }

    public FileDto findFileById(Integer id) {
        FileEntity fileEntity = fileRepository.findById(id).orElseThrow(() -> new FileHandlerException("Файл с таким id не был найден: " + id));
        return fileMapper.toDto(fileEntity);
    }

    @Getter
    enum ImageType {
        JPEG("image/jpeg"),
        JPG("image/jpg"),
        PNG("image/png"),
        GIF("image/gif");

        private final String fileType;

        ImageType(String fileType) {
            this.fileType = fileType;
        }

        public static boolean isImageType(String type) {
            for (ImageType imageType : values()) {
                if (imageType.getFileType().equalsIgnoreCase(type)) {
                    return true;
                }
            }
            return false;
        }
    }
}
