package ru.main.passcode.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OutgoingMessage {
    private long video_id;
    private String video; // полное имя файла
}
