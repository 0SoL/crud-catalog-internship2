package ru.rustam.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handeNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors() // запрашивает класс FieldError
                .stream() // собиарем поток данных из коллекции
                .map(error -> error.getField() + ": " + error.getDefaultMessage())   // пересобираем используем методы класса FieldError получаем строчку и сообщенеие которое я укзаал в валидаторе
                .toList();

        return ResponseEntity.badRequest().body(errors);
    }
    // для ловли ошибок по файлу
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handeIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


}
