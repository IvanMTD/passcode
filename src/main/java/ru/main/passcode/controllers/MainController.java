package ru.main.passcode.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.main.passcode.interfaces.MessageService;
import ru.main.passcode.models.Content;
import ru.main.passcode.services.RabbitMessageService;

import java.util.Date;

@Controller
public class MainController {
    private MessageService messageService;

    public MainController(RabbitMessageService messageService) {
        this.messageService = messageService;
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/")
    public String homePage(){
        //return "redirect:/help";
        return "home";
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/send")
    public String sendMessage(){
        Content content = new Content();
        content.setId(1);
        content.setFileSize(11232132);
        content.setFileName("My Name Is");
        content.setHashData("123213123123");
        content.setPlacedAt(new Date());

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "admin/login";
    }

    @GetMapping("/logout")
    public String performLogout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);
        return "redirect:/";
    }

    @GetMapping("/contact")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String contactPage(){
        return "info/contact";
    }

    @GetMapping("/help")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String helpPage(){
        return "info/help";
    }
}
