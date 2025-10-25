package com.example.datn.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dsojjxs1n",
                "api_key", "566951125354329",
                "api_secret", "a3ghRMifxxCzgLgY_IkU50ZCAtY"
        ));
    }
}

