package com.example.webdemo.controller;

import com.example.webdemo.model.User;
import com.example.webdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("genders", new String[]{"Erkek", "Kadın", "Diğer"});
        model.addAttribute("occupations", new String[]{
            "Öğrenci", "Mühendis", "Doktor", "Öğretmen", "Avukat", 
            "Muhasebeci", "Yazılımcı", "Diğer"
        });
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", new String[]{"Erkek", "Kadın", "Diğer"});
            model.addAttribute("occupations", new String[]{
                "Öğrenci", "Mühendis", "Doktor", "Öğretmen", "Avukat", 
                "Muhasebeci", "Yazılımcı", "Diğer"
            });
            return "register";
        }
        
        try {
            userService.registerUser(user);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("genders", new String[]{"Erkek", "Kadın", "Diğer"});
            model.addAttribute("occupations", new String[]{
                "Öğrenci", "Mühendis", "Doktor", "Öğretmen", "Avukat", 
                "Muhasebeci", "Yazılımcı", "Diğer"
            });
            return "register";
        }
    }
} 