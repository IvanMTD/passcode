package ru.main.passcode.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.main.passcode.dto.PersonDTO;
import ru.main.passcode.services.AuthorityService;
import ru.main.passcode.services.PersonService;
import ru.main.passcode.validations.PersonValidator;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class UserController {

    private final PersonService personService;
    private final AuthorityService authorityService;
    private final PersonValidator personValidator;

    private final int itemOnPage = 5;
    private int totalPages;

    @GetMapping("/users/{page}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String usersPage(Model model, @PathVariable(name = "page") int page){
        prepareTotalPage();
        prepareModel(page,model);
        return "admin/users";
    }

    @PostMapping("/users/add")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String addPerson(Model model, @ModelAttribute(name = "person") @Valid PersonDTO personDTO, Errors errors, @RequestParam(name = "cpAdd") int page){
        personValidator.validate(personDTO, errors);
        if(errors.hasErrors()){
            Pageable pageable = PageRequest.of(page,itemOnPage);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", page);
            model.addAttribute("itemOnPage",itemOnPage);
            model.addAttribute("authorities", authorityService.findAll());
            model.addAttribute("persons", personService.findAllByPageable(pageable));
            return "admin/users";
        }
        personService.save(personDTO);
        prepareTotalPage();
        return "redirect:/admin/users/" + page;
    }

    @PostMapping("/users/delete/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String deleteUser(Model model, @PathVariable(name = "id") long id, @RequestParam(name = "cpDelete") int page){
        personService.delete(id);
        prepareTotalPage();
        if(totalPages < (page + 1)){
            page = page - 1;
        }
        prepareModel(page,model);
        return "admin/users";
    }

    private void prepareModel(int page, Model model){
        Pageable pageable = PageRequest.of(page,itemOnPage);
        model.addAttribute("persons", personService.findAllByPageable(pageable));
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("itemOnPage",itemOnPage);
        model.addAttribute("person",new PersonDTO());
        model.addAttribute("authorities", authorityService.findAll());
    }

    private void prepareTotalPage(){
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
    }
}
