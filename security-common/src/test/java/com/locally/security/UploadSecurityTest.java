package com.locally.security;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
class UploadSecurityTest {
    @Test void spoofedMimeTypesAndSvgAreRejected() {
        assertThrows(ResponseStatusException.class, () -> Uploads.image(new MockMultipartFile("image", "image.png", "image/png", "<script>alert(1)</script>".getBytes())));
        assertThrows(ResponseStatusException.class, () -> Uploads.image(new MockMultipartFile("image", "image.svg", "image/svg+xml", "<svg onload='alert(1)'/>".getBytes())));
    }
    @Test void validPngIsAccepted() throws Exception {
        var bytes = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(new java.awt.image.BufferedImage(2, 2, java.awt.image.BufferedImage.TYPE_INT_RGB), "png", bytes);
        assertDoesNotThrow(() -> Uploads.image(new MockMultipartFile("image", "image.png", "image/png", bytes.toByteArray())));
    }
    @Test void repeatedAttemptsAreBounded() {
        String key = "test:" + java.util.UUID.randomUUID();
        AttemptLimiter.check(key, 2, 900); AttemptLimiter.check(key, 2, 900);
        assertThrows(ResponseStatusException.class, () -> AttemptLimiter.check(key, 2, 900));
        AttemptLimiter.clear(key);
    }
}
