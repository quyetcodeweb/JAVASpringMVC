package com.example.demo;

import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.model.Product;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private ProductService service;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", service.getAllProducts());
        return "index";
    }

    @PostMapping("/add")
    public String addProduct(@RequestParam String name, @RequestParam double price) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        service.addProduct(p);
        return "redirect:/";
    }
}
