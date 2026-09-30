package com.locally.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class Uploads {
    private Uploads() {}
    public static void image(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() > 10L * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image must be at most 10 MB");
        }
        try (var input = ImageIO.createImageInputStream(file.getInputStream())) {
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IOException("Unsupported image");
            var reader = readers.next();
            try {
                if (!java.util.Set.of("JPEG", "JPG", "PNG", "GIF").contains(reader.getFormatName().toUpperCase(java.util.Locale.ROOT))) {
                    throw new IOException("Unsupported image");
                }
                reader.setInput(input);
                if ((long) reader.getWidth(0) * reader.getHeight(0) > 25_000_000) throw new IOException("Too many pixels");
            } finally { reader.dispose(); }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload a valid PNG, JPEG, or GIF image");
        }
    }
}
