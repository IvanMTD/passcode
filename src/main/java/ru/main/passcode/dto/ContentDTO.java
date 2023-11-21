package ru.main.passcode.dto;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
public class ContentDTO {
    private long id;
    private String fileName;
    private String fullPath;
    private String placedAt;
    private long fileSize;
    private List<String> images;

    public String getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(Date placedAt) {
        this.placedAt = new SimpleDateFormat("dd:MM:yyyy").format(placedAt);
    }
}
