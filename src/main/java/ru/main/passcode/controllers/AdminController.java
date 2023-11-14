package ru.main.passcode.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.main.passcode.dto.ContentDTO;
import ru.main.passcode.dto.IncomingMessage;
import ru.main.passcode.dto.OutgoingMessage;
import ru.main.passcode.dto.PersonDTO;
import ru.main.passcode.interfaces.RabbitMessageService;
import ru.main.passcode.models.Content;
import ru.main.passcode.services.AuthorityService;
import ru.main.passcode.services.ContentService;
import ru.main.passcode.services.PersonService;
import ru.main.passcode.validations.PersonValidator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RabbitMessageService messageService;

    private final AuthorityService authorityService;
    private final PersonService personService;
    private final ContentService contentService;
    private final PersonValidator personValidator;

    private int currentPage = 0;
    private int currentPage2 = 0;
    private int itemOnPage = 5;
    private int itemOnPage2 = 5;
    private int totalPages;
    private int totalPages2;

    private long currentTrackedElement = -1;

    private List<IncomingMessage> messages = new ArrayList<>(); // времянка в будущем можно посадить в базу данных
    private List<String> images = new ArrayList<>();

    private boolean fileAdded;
    private boolean fileDeleted;


    // ================================================ USERS ==========================================================

    @GetMapping("/users/{page}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String usersPage(Model model, @PathVariable(name = "page") int page){
        int personCount = personService.findAll().size();
        if(personCount == 0){
            totalPages = 1;
        }else {
            if (personCount % itemOnPage > 0) {
                if (personCount / itemOnPage == 0) {
                    totalPages = 1;
                } else {
                    totalPages = (personCount / itemOnPage) + 1;
                }
            } else {
                totalPages = personCount / itemOnPage;
            }
        }
        currentPage = page;
        Pageable pageable = PageRequest.of(currentPage,itemOnPage);

        model.addAttribute("persons", personService.findAllByPageable(pageable));
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("itemOnPage",itemOnPage);
        model.addAttribute("person",new PersonDTO());
        model.addAttribute("authorities", authorityService.findAll());
        return "admin/users";
    }

    @PostMapping("/users/add")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String addPerson(Model model, @ModelAttribute(name = "person") @Valid PersonDTO personDTO, Errors errors){
        personValidator.validate(personDTO, errors);
        if(errors.hasErrors()){
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("itemOnPage",itemOnPage);
            Pageable pageable = PageRequest.of(currentPage,itemOnPage);
            model.addAttribute("persons", personService.findAllByPageable(pageable));
            model.addAttribute("authorities", authorityService.findAll());
            return "admin/users";
        }
        personService.save(personDTO);
        return "redirect:/admin/users/" + currentPage;
    }

    @PostMapping("/users/delete/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String deleteUser(Model model, @PathVariable(name = "id") long id){
        personService.delete(id);
        currentPage = 0;
        Pageable pageable = PageRequest.of(currentPage,itemOnPage);
        model.addAttribute("persons", personService.findAllByPageable(pageable));
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("itemOnPage",itemOnPage);
        model.addAttribute("person",new PersonDTO());
        model.addAttribute("authorities", authorityService.findAll());
        return "admin/users";
    }

    // =================================================== FILES =======================================================

    @GetMapping("/files/{page}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String filesPage(Model model, @PathVariable(name = "page") int page){
        int fileCount = contentService.findAll().size();
        if(fileCount == 0){
            totalPages2 = 1;
        }else {
            if (fileCount % itemOnPage2 > 0) {
                if (fileCount / itemOnPage2 == 0) {
                    totalPages2 = 1;
                } else {
                    totalPages2 = (fileCount / itemOnPage2) + 1;
                }
            } else {
                totalPages2 = fileCount / itemOnPage2;
            }
        }
        currentPage2 = page;

        images = contentService.findByIdContentDTO(currentTrackedElement).getImages();
        Pageable pageable = PageRequest.of(currentPage2,itemOnPage2);
        model.addAttribute("totalPages", totalPages2);
        model.addAttribute("currentPage", currentPage2);
        model.addAttribute("itemOnPage",itemOnPage2);
        model.addAttribute("fileInform",contentService.findAllByPageable(pageable));
        model.addAttribute("images",images);
        return "admin/files";
    }

    @PostMapping("/files/add")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String addFiles(Model model, @RequestParam(name = "file")MultipartFile file){
        fileAdded = true;
        Content content = contentService.save(file);
        OutgoingMessage outgoingMessage = new OutgoingMessage();
        outgoingMessage.setVideo_id(content.getId());
        outgoingMessage.setVideo(content.getFileName());
        messageService.sendMessage(outgoingMessage);

        Pageable pageable = PageRequest.of(currentPage2,itemOnPage2);
        model.addAttribute("totalPages", totalPages2);
        model.addAttribute("currentPage", currentPage2);
        model.addAttribute("itemOnPage",itemOnPage2);
        model.addAttribute("fileInform",contentService.findAllByPageable(pageable));
        model.addAttribute("images",images);

        return "redirect:/admin/files/" + currentPage2;
    }

    @PostMapping("/files/delete/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String deleteFile(Model model, @PathVariable(name = "id") long id){
        fileDeleted = true;
        contentService.delete(id);
        currentPage2 = 0;
        images = new ArrayList<>();
        Pageable pageable = PageRequest.of(currentPage2,itemOnPage2);
        model.addAttribute("totalPages", totalPages2);
        model.addAttribute("currentPage", currentPage2);
        model.addAttribute("itemOnPage",itemOnPage2);
        model.addAttribute("fileInform",contentService.findAllByPageable(pageable));
        model.addAttribute("images",images);
        return "redirect:/admin/files/" + currentPage2;
    }

    @PostMapping("/files/get/photo/{id}")
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
    }

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

    @MessageMapping("/connection")
    @SendTo("/send/content")
    public String connection() throws JsonProcessingException {
        /*List<ContentDTO> contents = contentService.findAllByPageable(PageRequest.of(currentPage2,itemOnPage2));
        ContentDTO[] array = new ContentDTO[contents.size()];
        for(int i=0; i<contents.size(); i++){
            array[i] = contents.get(i);
        }
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(array);*/

        return "";
    }
}
