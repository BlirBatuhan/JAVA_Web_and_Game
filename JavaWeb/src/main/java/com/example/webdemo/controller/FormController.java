package com.example.webdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class FormController {

    @GetMapping("/form")
    public String showForm(Model model) {
        // Örnek dropdown seçenekleri
        List<String> sehirler = Arrays.asList("İstanbul", "Ankara", "İzmir", "Bursa", "Antalya");
        List<String> meslekler = Arrays.asList("Mühendis", "Doktor", "Öğretmen", "Avukat", "Diğer");
        
        model.addAttribute("sehirler", sehirler);
        model.addAttribute("meslekler", meslekler);
        
        return "form";
    }

    @PostMapping("/submit")
    public String handleSubmit(@RequestParam String ad,
                             @RequestParam String sehir,
                             @RequestParam String meslek,
                             Model model) {
        // Form verilerini işle
        model.addAttribute("ad", ad);
        model.addAttribute("sehir", sehir);
        model.addAttribute("meslek", meslek);
        
        return "result";
    }
} 