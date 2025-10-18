package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // Thư mục upload ngoài JAR
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/images";

    // 🧩 Hiển thị danh sách sản phẩm
    @GetMapping
    public String listProducts(Model model, Locale locale) {
        List<Product> products = productRepository.findAll();
        boolean isEnglish = locale.getLanguage().equals("en");

        for (Product p : products) {
            // đổi tên
            if (isEnglish && p.getNameEn() != null && !p.getNameEn().isEmpty()) {
                p.setName(p.getNameEn());
            }

            // đổi giá
            if (isEnglish && p.getPriceUsd() != null && p.getPriceUsd() > 0) {
                p.setPrice(p.getPriceUsd());
            }

            // đổi note
            if (p.getNote() != null && p.getNote().contains("|")) {
                String[] notes = p.getNote().split("\\|");
                p.setNote(isEnglish ? notes.length > 1 ? notes[1] : notes[0] : notes[0]);
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("locale", locale.getLanguage());
        return "index";
    }

    // 🧩 Thêm sản phẩm với upload ảnh
    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
            @RequestParam("imgFile") MultipartFile imgFile) {

        try {
            // --- Dịch tự động ---
            product.setNameEn(translateText(product.getName(), "en"));
            if (product.getNote() != null && !product.getNote().isEmpty()) {
                product.setNote(product.getNote() + "|" + translateText(product.getNote(), "en"));
            }

            // --- Tính giá USD ---
            product.setPriceUsd(product.getPrice() / 25000.0);

            // --- Upload ảnh ---
            if (!imgFile.isEmpty()) {
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists())
                    uploadFolder.mkdirs();

                String filename = StringUtils.cleanPath(imgFile.getOriginalFilename());
                File saveFile = new File(uploadFolder, filename);
                imgFile.transferTo(saveFile);

                // Lưu đường dẫn hiển thị trong HTML
                product.setImg("/uploads/images/" + filename);
            }

            productRepository.save(product);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/products";
    }

    // 🧠 Hàm dịch tự động (Google Translate API không chính thức)
    private String translateText(String text, String targetLang) {
        if (text == null || text.trim().isEmpty())
            return "";
        try {
            String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl="
                    + targetLang + "&dt=t&q=" + URLEncoder.encode(text, "UTF-8");

            java.net.URL url = new java.net.URL(urlStr);
            java.io.InputStreamReader reader = new java.io.InputStreamReader(url.openStream());
            StringBuilder result = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1)
                result.append((char) c);
            reader.close();

            // Kết quả JSON dạng [[["Dịch","Nguyên"]]]
            String[] parts = result.toString().split("\"");
            if (parts.length > 1)
                return parts[1];

        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }
}
