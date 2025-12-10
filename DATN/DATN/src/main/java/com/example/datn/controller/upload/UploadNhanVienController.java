package com.example.datn.controller.upload;

import com.example.datn.config.CloudinaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping // map ở method để support cả 2 route
public class UploadNhanVienController {

    private final CloudinaryService cloudinaryService;

    public UploadNhanVienController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    // ✅ Upload ảnh (support cả /admin/upload và /api/upload)
    @PostMapping({"/admin/upload", "/api/upload"})
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadFile(file);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    // ✅ Xoá ảnh theo publicId hoặc URL (support cả 2 route)
    @DeleteMapping({"/admin/upload/delete", "/api/upload/delete"})
    public ResponseEntity<?> deleteImage(@RequestParam("publicId") String publicIdOrUrl) {
        if (publicIdOrUrl == null || publicIdOrUrl.isBlank()) {
            return ResponseEntity.badRequest().body("publicId không được để trống");
        }

        try {
            // Nếu service bạn đang dùng có boolean:
            boolean ok = cloudinaryService.deleteFileReturnOk(publicIdOrUrl);

            if (ok) return ResponseEntity.ok("Xóa ảnh thành công");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Xóa ảnh thất bại (Ảnh không tồn tại hoặc lỗi mạng)");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi xoá ảnh: " + e.getMessage());
        }
    }
}
