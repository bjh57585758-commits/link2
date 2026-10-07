package com.miryang.restaurant.dto;

import com.miryang.restaurant.domain.Restaurant;
import java.time.LocalDateTime;
import java.util.List;

public record RestaurantDetail(Long id, String name, String category, String address, String phone,
                               String description, LocalDateTime createdAt, Double averageRating,
                               List<ReviewResponse> reviews) {
    public static RestaurantDetail of(Restaurant r, Double avg, List<ReviewResponse> reviews) {
        return new RestaurantDetail(r.getId(), r.getName(), r.getCategory(), r.getAddress(), r.getPhone(),
                r.getDescription(), r.getCreatedAt(), avg, reviews);
    }
}
