package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Locale;

@Controller
public class LanguageController {

    @Autowired
    private LocaleResolver localeResolver;

    @GetMapping("/change-lang")
    public String changeLanguage(@RequestParam("lang") String lang,
            HttpServletRequest request,
            HttpServletResponse response) {

        Locale newLocale = new Locale(lang);
        localeResolver.setLocale(request, response, newLocale);

        // ✅ Quay lại trang trước (reload mượt)
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/products");
    }
}
