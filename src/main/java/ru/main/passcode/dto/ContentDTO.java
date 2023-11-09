package ru.main.passcode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ContentDTO {
    private long id;
    @NotBlank(message = "Поле не может быть пустым")
    private String fileName;
    @NotNull(message = "Укажите файл")
    private MultipartFile dataFile;
}
