package ru.main.passcode.configurations;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class TestController {
    @GetMapping("/test")
    public int test(){
        System.out.println("Try found files");
        int num = 0;
        File dir = new File("./src/main/resources/static/result/0/");
        if(dir.isDirectory()){
            System.out.println("Directory " + dir.getAbsolutePath() + " exist");
            File[] files = dir.listFiles();
            if(files != null){
                System.out.println("Found " + files.length + " files");
                num = files.length;
            }
        }
        return num;
    }
}
