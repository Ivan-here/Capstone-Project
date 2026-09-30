package com.example.profileservice.profiles.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.locally.security.Access;
import com.locally.security.AttemptLimiter;
import com.locally.security.Uploads;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/profiles/me/images")
public class ImageUploadController {
    private final Cloudinary cloudinary;
    public ImageUploadController(@Value("${CLOUDINARY_CLOUD_NAME}") String cloud,
                                 @Value("${CLOUDINARY_API_KEY}") String key,
                                 @Value("${CLOUDINARY_API_SECRET}") String secret) {
        cloudinary = new Cloudinary(ObjectUtils.asMap("cloud_name", cloud, "api_key", key, "api_secret", secret, "secure", true));
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> upload(@RequestPart("image") MultipartFile file) throws IOException {
        AttemptLimiter.check("images:" + Access.userId(), 20, 3600);
        Uploads.image(file);
        var result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "image", "folder", "profiles", "format", "png"));
        return Map.of("url", result.get("secure_url").toString());
    }
}
