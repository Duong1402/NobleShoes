package com.example.datn.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File upload rỗng");
        }
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto")
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Upload file lên Cloudinary thất bại: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String fileIdOrUrl) {
        if (fileIdOrUrl == null || fileIdOrUrl.isBlank()) return;

        String publicId = extractPublicId(fileIdOrUrl);
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            System.out.println("Cloudinary destroy result = " + result);
        } catch (IOException e) {
            throw new RuntimeException("Xoá file trên Cloudinary thất bại: " + e.getMessage(), e);
        }
    }

    public boolean deleteFileReturnOk(String fileIdOrUrl) {
        if (fileIdOrUrl == null || fileIdOrUrl.isBlank()) return false;

        String publicId = extractPublicId(fileIdOrUrl);
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String extractPublicId(String fileIdOrUrl) {
        if (!fileIdOrUrl.contains("res.cloudinary.com")) {
            return fileIdOrUrl; // đã là publicId
        }

        String withoutQuery = fileIdOrUrl.split("\\?")[0];

        int uploadIdx = withoutQuery.indexOf("/upload/");
        if (uploadIdx > 0) {
            String afterUpload = withoutQuery.substring(uploadIdx + "/upload/".length());
            int firstSlash = afterUpload.indexOf('/');
            if (firstSlash > 0) {
                String folderAndFile = afterUpload.substring(firstSlash + 1);
                int dotIdx2 = folderAndFile.lastIndexOf('.');
                return (dotIdx2 > 0) ? folderAndFile.substring(0, dotIdx2) : folderAndFile;
            }
        }

        String filename = withoutQuery.substring(withoutQuery.lastIndexOf("/") + 1);
        int dotIdx = filename.lastIndexOf('.');
        return (dotIdx > 0) ? filename.substring(0, dotIdx) : filename;
    }

}
