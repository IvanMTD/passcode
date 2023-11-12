package ru.main.passcode.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.main.passcode.utils.ImageEncryptUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ResultService {
    public List<String> checkResult(long id){
        List<String> imgList = new ArrayList<>();
        File dir = new File("./src/main/resources/result/" + id);
        if(dir.isDirectory()){
            System.out.println("Directory: " + dir.getAbsolutePath());
            log.info("Directory: " + dir.getAbsolutePath());
            File[]images = dir.listFiles();
            if(images != null){
                System.out.println("Found " + images.length + " files");
                log.info("Found " + images.length + " files");
                int n = 0;
                for(File image : images){
                    try {
                        byte[] i = Files.readAllBytes(Path.of(image.getAbsolutePath()));
                        imgList.add(ImageEncryptUtil.getImgData(i));
                    } catch (IOException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                        log.info("Ошибка: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                    n++;
                    if(n>7){
                        break;
                    }
                }
            }
        }
        System.out.println("Total images in the container: " + imgList.size());
        log.info("Total images in the container: " + imgList.size());
        return imgList;
    }
}
