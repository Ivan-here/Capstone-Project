package com.example.verificationservice.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > 10L * 1024 * 1024) throw new IllegalArgumentException("Document must be at most 10 MB");
        try {
            byte[] prefix;
            try (var input = file.getInputStream()) { prefix = input.readNBytes(5); }
            boolean pdf = java.util.Arrays.equals(prefix, "%PDF-".getBytes(java.nio.charset.StandardCharsets.US_ASCII));
            if (!pdf) com.locally.security.Uploads.image(file);
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("type", "authenticated", "resource_type", "image", "folder", "verification"));
            return "private:" + uploadResult.get("public_id") + ":" + uploadResult.get("format");
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
    }
    public String downloadUrl(String reference) {
        if (reference == null) return null;
        // Old public documents require migration; never issue new public document links.
        if (!reference.startsWith("private:")) return null;
        String[] parts = reference.split(":", 3);
        if (parts.length != 3) return null;
        try {
            return cloudinary.privateDownload(parts[1], parts[2], ObjectUtils.asMap("type", "authenticated", "resource_type", "image",
                    "expires_at", java.time.Instant.now().getEpochSecond() + 300, "attachment", true));
        } catch (Exception e) { throw new IllegalStateException("Unable to sign document download"); }
    }
}
