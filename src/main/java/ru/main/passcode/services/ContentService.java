package ru.main.passcode.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.main.passcode.dto.ContentDTO;
import ru.main.passcode.models.Content;
import ru.main.passcode.repositories.ContentRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final ResultService resultService;

    public Content save(MultipartFile file){
        Content content = new Content();
        Content res = new Content();
        content.setFileName(file.getOriginalFilename());
        content.setFileSize(file.getSize());
        content.setHashData(String.valueOf(file.hashCode()));
        try {
            InputStream inputStream = file.getInputStream();
            File savedFile = new File("./src/main/resources/static/saved/" + file.getOriginalFilename());
            if(!savedFile.exists()) {
                FileUtils.copyInputStreamToFile(inputStream, savedFile);
                res = contentRepository.save(content);
                log.info("file saved with id " + res.getId());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(res.getId());

        return res;
    }

    public List<Content> findAll() {
        return contentRepository.findAll();
    }

    public List<ContentDTO> findAllByPageable(Pageable pageable){
        List<Content> contentList = contentRepository.findAllByOrderByPlacedAtDesc(pageable);
        List<ContentDTO> contentDTOList = new ArrayList<>();
        for(Content content : contentList){
            ContentDTO contentDTO = new ContentDTO();
            contentDTO.setId(content.getId());
            contentDTO.setFileName(content.getFileName());
            contentDTO.setFileSize(content.getFileSize());
            contentDTO.setPlacedAt(content.getPlacedAt());
            contentDTO.setImages(resultService.checkResult(contentDTO.getId()));
            contentDTOList.add(contentDTO);
        }
        return contentDTOList;
    }

    public void delete(long id) {
        Optional<Content> optionalContent = contentRepository.findById(id);
        if(optionalContent.isPresent()){

            Content content = optionalContent.get();
            File file = new File("./src/main/resources/static/saved/" + content.getFileName());
            if(file.exists()){
                if(file.delete()) {
                    File dir = new File("./src/main/resources/static/result/" + content.getId());
                    if(dir.isDirectory()){
                        File[] files = dir.listFiles();
                        if(files != null) {
                            for (File f : files) {
                                log.info("file " + f.getAbsolutePath() + " delete: " + f.delete());
                            }
                        }
                    }
                    log.info("directory " + dir.getAbsolutePath() + " delete: " + dir.delete());
                    contentRepository.deleteById(id);
                }
            }
        }
    }

    public Content findById(long id) {
        Optional<Content> optionalContent = contentRepository.findById(id);
        return optionalContent.orElse(null);
    }

    public ContentDTO findByIdContentDTO(long id) {
        Optional<Content> optionalContent = contentRepository.findById(id);
        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setImages(new ArrayList<>());
        if(optionalContent.isPresent()){
            Content content = optionalContent.get();
            contentDTO.setId(content.getId());
            contentDTO.setFileName(content.getFileName());
            contentDTO.setFileSize(content.getFileSize());
            contentDTO.setPlacedAt(content.getPlacedAt());
            contentDTO.setImages(resultService.checkResult(contentDTO.getId()));
        }
        return contentDTO;
    }
}
