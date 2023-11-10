package ru.main.passcode.services;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.main.passcode.models.Content;
import ru.main.passcode.repositories.ContentRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;

    public Content save(MultipartFile file){
        Content content = new Content();
        content.setFileName(file.getOriginalFilename());
        content.setFileSize(file.getSize());
        content.setHashData(String.valueOf(file.hashCode()));
        try {
            InputStream inputStream = file.getInputStream();
            File savedFile = new File("./src/main/resources/saved/" + file.getOriginalFilename());
            if(!savedFile.exists()) {
                FileUtils.copyInputStreamToFile(inputStream, savedFile);
                contentRepository.save(content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return content;
    }

    public List<Content> findAll() {
        return contentRepository.findAll();
    }

    public List<Content> findAllByPageable(Pageable pageable){
        return contentRepository.findAllByOrderByPlacedAtDesc(pageable);
    }

    public void delete(long id) {
        Optional<Content> optionalContent = contentRepository.findById(id);
        if(optionalContent.isPresent()){
            Content content = optionalContent.get();
            File file = new File("./src/main/resources/saved/" + content.getFileName());
            if(file.exists()){
                if(file.delete()) {
                    contentRepository.deleteById(id);
                }
            }
        }
    }
}
