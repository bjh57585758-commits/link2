package com.miryang.restaurant.dto;

import com.miryang.restaurant.domain.Review;
import java.time.LocalDateTime;

public record ReviewResponse(Long id, String author, int rating, String content, LocalDateTime createdAt) {
    public static ReviewResponse from(Review r) {
        return new ReviewResponse(r.getId(), r.getAuthor(), r.getRating(), r.getContent(), r.getCreatedAt());
    }
}
