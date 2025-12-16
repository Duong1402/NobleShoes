package com.example.datn.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
public class DebugUploadController {

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    @GetMapping("/public/debug/upload")
    public Object debugUpload() {
        Path p = Paths.get(uploadDir).toAbsolutePath().normalize();
        File f = p.toFile();

        return new java.util.LinkedHashMap<String, Object>() {{
            put("app.upload-dir", uploadDir);
            put("resolvedPath", p.toString());
            put("exists", f.exists());
            put("isDirectory", f.isDirectory());
            put("files", f.exists() && f.isDirectory()
                    ? Arrays.stream(f.listFiles() == null ? new File[0] : f.listFiles())
                    .map(x -> x.getName())
                    .collect(Collectors.toList())
                    : java.util.List.of());
        }};
    }
}
