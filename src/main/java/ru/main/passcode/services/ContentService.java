package ru.main.passcode.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.main.passcode.dto.ContentDTO;
import ru.main.passcode.models.Content;
import ru.main.passcode.repositories.ContentRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;

    public Content save(ContentDTO contentDTO){
        Content content = new Content();
        content.setFileName(contentDTO.getFileName());
        try {
            content.setDataFile(contentDTO.getDataFile().getBytes());
            content.setFileSize(content.getDataFile().length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return contentRepository.save(content);
    }
}
