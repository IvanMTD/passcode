package ru.main.passcode.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.main.passcode.dto.ContentDTO;
import ru.main.passcode.dto.PersonDTO;
import ru.main.passcode.services.AuthorityService;
import ru.main.passcode.services.ContentService;
import ru.main.passcode.services.PersonService;
import ru.main.passcode.validations.PersonValidator;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthorityService authorityService;
    private final PersonService personService;
    private final ContentService contentService;
    private final PersonValidator personValidator;

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String usersPage(Model model){
        Pageable pageable = PageRequest.of(0,10);
        model.addAttribute("persons", personService.findAllByPageable(pageable));
        model.addAttribute("person",new PersonDTO());
        model.addAttribute("authorities", authorityService.findAll());
        return "admin/users";
    }

    @PostMapping("/users")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String addPerson(Model model, @ModelAttribute(name = "person") @Valid PersonDTO personDTO, Errors errors){
        personValidator.validate(personDTO, errors);
        if(errors.hasErrors()){
            Pageable pageable = PageRequest.of(0,10);
            model.addAttribute("persons", personService.findAllByPageable(pageable));
            model.addAttribute("authorities", authorityService.findAll());
            return "admin/users";
        }
        personService.save(personDTO);
        return "redirect:/admin/users";
    }

    @GetMapping("/files")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String filesPage(Model model){
        model.addAttribute("content", new ContentDTO());
        return "admin/files";
    }

    @PostMapping("/files")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String addFiles(Model model, @ModelAttribute(name = "content") @Valid ContentDTO contentDTO, Errors errors){
       if(errors.hasErrors()){
           return "admin/files";
       }
       contentService.save(contentDTO);
       return "redirect:/admin/files";
    }
}
