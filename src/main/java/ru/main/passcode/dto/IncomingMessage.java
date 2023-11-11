package ru.main.passcode.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IncomingMessage {
    private long video_id;
    private String images; // директория в которой лежат файлы
}
