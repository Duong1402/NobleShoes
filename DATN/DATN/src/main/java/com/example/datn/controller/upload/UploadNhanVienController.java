// src/main/java/com/example/datn/controller/Upload/UploadNhanVienController.java
package com.example.datn.controller.Upload;

import com.example.datn.Config.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:5173")
public class UploadNhanVienController {

    private final CloudinaryService cloudinaryService;

    public UploadNhanVienController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    // Upload ảnh, trả về URL
    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // giả sử CloudinaryService.uploadFile trả về String là URL
            String imageUrl = cloudinaryService.uploadFile(file);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    // Xoá ảnh – deleteFile hiện đang trả về void, nên KHÔNG gán vào boolean nữa
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteImage(@RequestParam("publicId") String publicId) {
        try {
            // deleteFile là void => chỉ cần gọi, nếu không ném exception thì coi như thành công
            cloudinaryService.deleteFile(publicId);

            return ResponseEntity.ok("Đã xoá ảnh thành công!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Lỗi xoá ảnh: " + e.getMessage());
        }
    }
}