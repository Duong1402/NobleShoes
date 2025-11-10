//điều khiển upload ảnh lên cloud
package com.example.datn.controller.upload;

import com.example.datn.config.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin/upload")
public class UploadNhanVienController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadFile(file);
            return ResponseEntity.ok().body(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteImage(@RequestParam("publicId") String publicId) {
        try {
            boolean deleted = cloudinaryService.deleteFile(publicId);
            if (deleted) {
                return ResponseEntity.ok("Đã xoá ảnh thành công!");
            } else {
                return ResponseEntity.badRequest().body("Không thể xoá ảnh!");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi xoá ảnh: " + e.getMessage());
        }
    }

}
