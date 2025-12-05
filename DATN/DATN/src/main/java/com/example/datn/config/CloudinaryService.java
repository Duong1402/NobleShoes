package com.example.datn.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Upload 1 file lên Cloudinary, trả về URL ảnh.
     * Controller đang gọi: cloudinaryService.uploadFile(file)
     */
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File upload rỗng");
        }
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.emptyMap()
            );
            // Cloudinary trả về nhiều field, trong đó "secure_url" hoặc "url"
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Upload file lên Cloudinary thất bại: " + e.getMessage(), e);
        }
    }

    /**
     * Xoá file trên Cloudinary theo publicId hoặc URL đầy đủ.
     * Dùng cho: cloudinaryService.deleteFile(...)
     */
    public void deleteFile(String fileIdOrUrl) {
        if (fileIdOrUrl == null || fileIdOrUrl.isBlank()) {
            return;
        }

        String publicId = extractPublicId(fileIdOrUrl);

        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            System.out.println("Cloudinary destroy result = " + result);
        } catch (IOException e) {
            throw new RuntimeException("Xoá file trên Cloudinary thất bại: " + e.getMessage(), e);
        }
    }

    /**
     * Nếu truyền vào URL => cắt lấy publicId; nếu truyền sẵn publicId thì trả lại luôn.
     * Ví dụ URL:
     * https://res.cloudinary.com/xxx/image/upload/v123456789/nhanvien/abcxyz.jpg
     * => publicId = nhanvien/abcxyz
     */
    private String extractPublicId(String fileIdOrUrl) {
        // Nếu không phải URL Cloudinary thì xem như đã là publicId
        if (!fileIdOrUrl.contains("res.cloudinary.com")) {
            return fileIdOrUrl;
        }

        // Bỏ query string
        String withoutQuery = fileIdOrUrl.split("\\?")[0];

        // Lấy phần sau /upload/
        int uploadIdx = withoutQuery.indexOf("/upload/");
        if (uploadIdx > 0) {
            String afterUpload = withoutQuery.substring(uploadIdx + "/upload/".length());
            // afterUpload: v123456789/nhanvien/abcxyz.jpg
            int firstSlash = afterUpload.indexOf('/');
            if (firstSlash > 0) {
                String folderAndFile = afterUpload.substring(firstSlash + 1); // nhanvien/abcxyz.jpg
                int dotIdx2 = folderAndFile.lastIndexOf('.');
                return (dotIdx2 > 0) ? folderAndFile.substring(0, dotIdx2) : folderAndFile;
            }
        }

        // fallback: chỉ cắt tên file
        String filename = withoutQuery.substring(withoutQuery.lastIndexOf("/") + 1);
        int dotIdx = filename.lastIndexOf('.');
        return (dotIdx > 0) ? filename.substring(0, dotIdx) : filename;
    }
}