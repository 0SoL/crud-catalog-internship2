package ru.rustam.catalog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.rustam.catalog.dto.FileDto;
import ru.rustam.catalog.service.FileService;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FileDto uploadFile(@RequestPart("file") MultipartFile file) throws IOException {
        return fileService.save(file);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FileDto findById(@PathVariable Integer id) {
        return fileService.findFileById(id);
    }


}
