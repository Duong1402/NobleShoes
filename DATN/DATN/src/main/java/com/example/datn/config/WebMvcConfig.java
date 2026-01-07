package com.example.datn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        // ✅ Windows-safe: "file:/C:/.../uploads/"
        String uploadLocation = "file:/" + uploadPath.toString().replace("\\", "/") + "/";

        // /uploads/** -> ./uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(3600);

        // /images/** -> ưu tiên static/images, không có thì fallback sang uploads
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/", uploadLocation)
                .setCachePeriod(3600);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Nếu FE là SPA (Vue/React) và dùng các route /public/**,
        // thì forward về index.html để FE router xử lý.

        // chi tiết sản phẩm
        registry.addViewController("/public/chi-tiet-san-pham/{id}")
                .setViewName("forward:/index.html");
        registry.addViewController("/public/chi-tiet-san-pham/**")
                .setViewName("forward:/index.html");

        // ctsp rút gọn
        registry.addViewController("/public/ctsp/{id}")
                .setViewName("forward:/index.html");
        registry.addViewController("/public/ctsp/**")
                .setViewName("forward:/index.html");

        // products/ctsp
        registry.addViewController("/public/products/ctsp/{id}")
                .setViewName("forward:/index.html");
        registry.addViewController("/public/products/ctsp/**")
                .setViewName("forward:/index.html");
    }
}
