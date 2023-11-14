package ru.main.passcode.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.main.passcode.dto.ContentDTO;

import java.util.ArrayList;
import java.util.List;

@Component
public class DirectoryMonitor {

    /*@Scheduled(fixedDelay = 1000)
    public void monitorDirectory() throws Exception {

        List<ContentDTO> content = new ArrayList<>();
        ContentDTO c = new ContentDTO();
        c.setId(1);
        c.setFileName("My Name");
        c.setFullPath("TEST");
        c.setFileSize(15);
        c.setImages(new ArrayList<>());
        content.add(c);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(content);

    }*/
}
