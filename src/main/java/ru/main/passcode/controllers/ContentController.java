package ru.main.passcode.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.main.passcode.dto.ContentDTO;
import ru.main.passcode.dto.OutgoingMessage;
import ru.main.passcode.interfaces.RabbitMessageService;
import ru.main.passcode.models.Content;
import ru.main.passcode.models.Person;
import ru.main.passcode.services.ContentService;
import ru.main.passcode.services.PersonService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ContentController {

    private final SimpMessagingTemplate template;
    private final RabbitMessageService messageService;
    private final ContentService contentService;
    private int itemOnPage = 5;
    private int totalPages;

    private Map<Long,Integer> idSize = new HashMap<>();


    @GetMapping("/files/{page}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String filesPage(Model model, @PathVariable(name = "page") int page){
        prepareTotalPages();
        prepareModel(page,model);
        return "admin/files";
    }

    @PostMapping("/files/add")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String addFiles(Model model, @RequestParam(name = "file")MultipartFile file, @RequestParam(name = "cpAdd") int page) throws JsonProcessingException {
        rabbitSandMessage(contentService.save(file));
        prepareTotalPages();
        prepareModel(page,model);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        this.template.convertAndSend("/send/server", ow.writeValueAsString(0));
        return "redirect:/admin/files/" + page;
    }

    @PostMapping("/files/delete/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String deleteFile(Model model, @PathVariable(name = "id") long id, @RequestParam(name = "cpDel") int page) throws JsonProcessingException {
        //ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        //this.template.convertAndSend("/send/content",ow.writeValueAsString(new Message(Message.DELETED, contentService.findByIdContentDTO(id))));
        contentService.delete(id);
        idSize.put(id,0);
        prepareTotalPages();
        if(totalPages < (page + 1)){
            page = page - 1;
        }
        prepareModel(page,model);
        return "redirect:/admin/files/" + page;
    }

    /*@PostMapping("/files/get/photo/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String setupPhoto(Model model,@PathVariable(name = "id") long id){
        currentTrackedElement = id;
        images = new ArrayList<>(contentService.findByIdContentDTO(id).getImages());
        Pageable pageable = PageRequest.of(currentPage2,itemOnPage2);
        model.addAttribute("totalPages", totalPages2);
        model.addAttribute("currentPage", currentPage2);
        model.addAttribute("itemOnPage",itemOnPage2);
        model.addAttribute("fileInform",contentService.findAllByPageable(pageable));
        model.addAttribute("images",images);
        return "redirect:/admin/files/" + currentPage2;
    }*/

    @ResponseBody
    @RequestMapping("/files/src/main/resources/static/saved/{fileName}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public byte[] getFile(@PathVariable String fileName){
        File file = new File("./src/main/resources/static/saved/" + fileName);
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ResponseBody
    @RequestMapping("/files/src/main/resources/static/result/{id}/{fileName}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public byte[] getFile(@PathVariable String fileName, @PathVariable int id){
        File file = new File("./src/main/resources/static/result/" + id + "/" + fileName);
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @MessageMapping("/delete-element")
    @SendTo("/send/content")
    public String deleteElement(RequestMessage requestMessage) throws JsonProcessingException {
        System.out.println("DELETE ID: " + requestMessage.getId() + " | page " + requestMessage.page);
        ContentDTO onDelete = contentService.findByIdContentDTO(requestMessage.getId());
        List<ContentDTO> beforeList = contentService.findAllByPageable(PageRequest.of(requestMessage.getPage(),itemOnPage));
        contentService.delete(requestMessage.getId());
        prepareTotalPages();
        List<ContentDTO> afterList = contentService.findAllByPageable(PageRequest.of(requestMessage.getPage(),itemOnPage));
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        if(afterList.size() == beforeList.size()){
            System.out.println("Размеры равны значит после удаления осталось 5 файлов");
            ContentDTO onAdded = beforeList.get(4);
            return ow.writeValueAsString(new ResponseMessage(totalPages,requestMessage.getPage(),requestMessage.getPage(),onAdded,onDelete,ResponseMessage.DELETED));
        }else{
            if(afterList.size() == 0){
                System.out.println("На странице нету больше файлов");
                int page = requestMessage.getPage();
                if(page>0){
                    page = page - 1;
                }
                return ow.writeValueAsString(new ResponseMessage(totalPages,page,requestMessage.getPage(),null,onDelete,ResponseMessage.DELETED));
            }else {
                System.out.println("На странице еще есть файлы");
                return ow.writeValueAsString(new ResponseMessage(totalPages,requestMessage.getPage(),requestMessage.getPage(),null,onDelete,ResponseMessage.DELETED));
            }
        }
    }

    @MessageMapping("/answer")
    //@SendTo("/send/server")
    public void clientAnswer(@NonNull String page) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        int currentPage = Integer.parseInt(page.substring(1,page.length()-1)) - 1;
        this.template.convertAndSend("/send/server", ow.writeValueAsString(new DataMessage(currentPage, totalPages, contentService.findAllByPageable(PageRequest.of(currentPage, itemOnPage)))));
    }
    @Scheduled(fixedDelay = 1000)
    public void monitoring() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        File directory = new File("./src/main/resources/static/result");
        if(directory.isDirectory()){
            File[]directories = directory.listFiles();
            for(File dir : directories){
                if(dir.exists()){
                    long id = Long.parseLong(dir.getName());
                    int size = dir.listFiles().length;
                    if(!idSize.containsKey(id)){
                        //
                        idSize.put(id,size);
                    }else{
                        if(idSize.get(id) == 0){
                            //
                            idSize.remove(id);
                        }
                    }
                }
            }
        }
    }

    private void prepareTotalPages(){
        int fileCount = contentService.findAll().size();
        if(fileCount == 0){
            totalPages = 1;
        }else {
            if (fileCount % itemOnPage > 0) {
                if (fileCount / itemOnPage == 0) {
                    totalPages = 1;
                } else {
                    totalPages = (fileCount / itemOnPage) + 1;
                }
            } else {
                totalPages = fileCount / itemOnPage;
            }
        }
    }

    private void prepareModel(int page, Model model){
        Pageable pageable = PageRequest.of(page,itemOnPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("itemOnPage",itemOnPage);
        model.addAttribute("fileInform",contentService.findAllByPageable(pageable));
        model.addAttribute("images",new ArrayList<String>()); // ВРЕМЕННО!
    }

    private void rabbitSandMessage(Content content) {
        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.setVideo_id(content.getId());
        outgoingMessage.setVideo(content.getFileName());
        messageService.sendMessage(outgoingMessage);
    }

    @Data
    static class ResponseMessage {
        private static final int ADDED = 1;
        private static final int DELETED = 2;

        private int status;
        private int totalPage;
        private int currentPage;
        private int oldPage;
        private ContentDTO onAdd;
        private ContentDTO onDelete;

        public ResponseMessage(int totalPage, int currentPage, int oldPage, ContentDTO onAdd, ContentDTO onDelete, int status) {
            this.totalPage = totalPage;
            this.currentPage = currentPage;
            this.oldPage = oldPage;
            this.onAdd = onAdd;
            this.onDelete = onDelete;
            this.status = status;

            System.out.println(
                    "TOTAL: " + this.totalPage +"\n" +
                    "CURRENT: " + this.currentPage +"\n" +
                    "OLD: " + this.oldPage +"\n"
            );
        }
    }

    @Data
    static class RequestMessage{
        private int id;
        private int page;
        public RequestMessage(int id, int page) {
            this.id = id;
            this.page = page;
        }
    }

    @Data
    static class DataMessage{
        private int currentPage;
        private int totalPages;
        private List<ContentDTO> content;

        public DataMessage(int currentPage, int totalPages, List<ContentDTO> content) {
            System.out.println("Размер листа: " + content.size() + " текущая страница " + currentPage);
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.content = content;
        }
    }
}
