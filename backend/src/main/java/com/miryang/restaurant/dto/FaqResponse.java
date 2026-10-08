package com.miryang.restaurant.dto;

import com.miryang.restaurant.domain.Faq;
import java.time.LocalDateTime;

public record FaqResponse(Long id, String author, String title, String question, String answer,
                          LocalDateTime answeredAt, LocalDateTime createdAt) {
    public static FaqResponse from(Faq f) {
        return new FaqResponse(f.getId(), f.getAuthor(), f.getTitle(), f.getQuestion(), f.getAnswer(),
                f.getAnsweredAt(), f.getCreatedAt());
    }
}
