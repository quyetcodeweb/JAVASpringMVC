package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ===================== Ngôn ngữ =====================
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(java.util.Locale.forLanguageTag("vi"));
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    // ===================== Static resource cho ảnh upload =====================
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Thư mục lưu ảnh upload (trong project hoặc ngoài project)
        String uploadPath = Paths.get(System.getProperty("user.dir"), "uploads/images").toUri().toString();
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations(uploadPath);
    }
}
