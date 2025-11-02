package com.tranthanhsang.example304.security.services;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class FileUploadService {
    private static final String UPLOAD_DIR = "uploads/";

    // ✅ Hàm xóa ảnh
    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isEmpty())
                return;

            // Nếu imageUrl là "/images/xxx.png" thì bỏ phần "/images/"
            String fileName = imageUrl.replace("/images/", "");
            Path path = Paths.get(UPLOAD_DIR, fileName);

            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("🗑️ Đã xóa ảnh: " + fileName);
            } else {
                System.out.println("⚠️ Không tìm thấy file: " + path);
            }
        } catch (IOException e) {
            System.err.println("⚠️ Lỗi khi xóa ảnh: " + e.getMessage());
        }
    }
}
