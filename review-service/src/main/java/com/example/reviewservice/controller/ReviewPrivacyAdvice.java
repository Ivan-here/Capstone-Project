package com.example.reviewservice.controller;
import com.example.reviewservice.model.Review;
import com.locally.security.Access;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
@RestControllerAdvice(assignableTypes = ReviewController.class)
public class ReviewPrivacyAdvice implements ResponseBodyAdvice<Object> {
    public boolean supports(MethodParameter method, Class<? extends HttpMessageConverter<?>> converter) { return true; }
    public Object beforeBodyWrite(Object body, MethodParameter method, MediaType type, Class<? extends HttpMessageConverter<?>> converter, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Review review) redact(review);
        if (body instanceof java.util.List<?> list) list.forEach(item -> { if (item instanceof Review review) redact(review); });
        return body;
    }
    private void redact(Review review) {
        if (!Access.privileged() && !Access.owns(review.getReviewerId())) {
            review.setOrderId(null);
            if (review.isAnonymous()) review.setReviewerId(null);
        }
    }
}
