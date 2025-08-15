package ru.rustam.catalog.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FileDto {
    private Integer id;
    private String filepath;
    private String name;
    private String type;

}
