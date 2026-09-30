package com.example.verificationservice.Controller;
import com.example.verificationservice.Model.Verification;
import com.example.verificationservice.Service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
@RestControllerAdvice(assignableTypes = VerificationController.class)
@RequiredArgsConstructor
public class VerificationDocumentAdvice implements ResponseBodyAdvice<Object> {
    private final CloudinaryService cloudinary;
    public boolean supports(MethodParameter method, Class<? extends HttpMessageConverter<?>> converter) { return true; }
    public Object beforeBodyWrite(Object body, MethodParameter method, MediaType type, Class<? extends HttpMessageConverter<?>> converter, ServerHttpRequest request, ServerHttpResponse response) {
        response.getHeaders().setCacheControl("no-store");
        if (body instanceof Verification verification) sign(verification);
        if (body instanceof java.util.List<?> list) list.forEach(item -> { if (item instanceof Verification verification) sign(verification); });
        return body;
    }
    private void sign(Verification verification) { verification.setDocumentUrl(cloudinary.downloadUrl(verification.getDocumentUrl())); }
}
