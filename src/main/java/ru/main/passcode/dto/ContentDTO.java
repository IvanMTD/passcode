package ru.main.passcode.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ContentDTO {
    private long id;
    private String fileName;
    private String fullPath;
    private Date placedAt;
    private long fileSize;
    private List<String> images;
}
